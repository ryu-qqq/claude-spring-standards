package com.ryuqq.domain.feedbackqueue.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackStatusTransitionException;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackPayload;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.ReviewNotes;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FeedbackQueue Aggregate 테스트")
class FeedbackQueueTest {

    private static final Instant NOW = Instant.parse("2025-01-20T10:00:00Z");

    @Nested
    @DisplayName("forNew 팩토리 메서드")
    class ForNewTest {

        @Test
        @DisplayName("ADD 타입은 targetId가 null이어도 생성 가능")
        void forNew_AddType_WithoutTargetId_ShouldSucceed() {
            // when
            FeedbackQueue feedback =
                    FeedbackQueue.forNew(
                            FeedbackTargetType.RULE_EXAMPLE,
                            null,
                            FeedbackType.ADD,
                            FeedbackPayload.of("{\"code\": \"test\"}"),
                            NOW);

            // then
            assertThat(feedback.isNew()).isTrue();
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.PENDING);
            assertThat(feedback.targetType()).isEqualTo(FeedbackTargetType.RULE_EXAMPLE);
            assertThat(feedback.riskLevel()).isEqualTo(RiskLevel.SAFE);
        }

        @Test
        @DisplayName("MODIFY 타입은 targetId가 필수")
        void forNew_ModifyType_WithoutTargetId_ShouldThrow() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    FeedbackQueue.forNew(
                                            FeedbackTargetType.CODING_RULE,
                                            null,
                                            FeedbackType.MODIFY,
                                            FeedbackPayload.of("{\"code\": \"test\"}"),
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("targetId is required");
        }

        @Test
        @DisplayName("DELETE 타입은 targetId가 필수")
        void forNew_DeleteType_WithoutTargetId_ShouldThrow() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    FeedbackQueue.forNew(
                                            FeedbackTargetType.CHECKLIST_ITEM,
                                            null,
                                            FeedbackType.DELETE,
                                            FeedbackPayload.of("{\"reason\": \"obsolete\"}"),
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("targetId is required");
        }

        @Test
        @DisplayName("CODING_RULE 타입은 MEDIUM 위험도로 생성됨")
        void forNew_CodingRuleType_ShouldHaveMediumRiskLevel() {
            // when
            FeedbackQueue feedback =
                    FeedbackQueue.forNew(
                            FeedbackTargetType.CODING_RULE,
                            null,
                            FeedbackType.ADD,
                            FeedbackPayload.of("{\"rule\": \"test\"}"),
                            NOW);

            // then
            assertThat(feedback.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }
    }

    @Nested
    @DisplayName("llmApprove 상태 전이")
    class LlmApproveTest {

        @Test
        @DisplayName("PENDING → LLM_APPROVED 전이 성공")
        void llmApprove_FromPending_ShouldSucceed() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.pendingSafeFeedback();
            feedback.assignId(FeedbackQueueId.of(1L));

            // when
            feedback.llmApprove(ReviewNotes.of("Looks good"), NOW.plusSeconds(60));

            // then
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.LLM_APPROVED);
            assertThat(feedback.reviewNotesValue()).isEqualTo("Looks good");
        }

        @Test
        @DisplayName("LLM_APPROVED 상태에서 llmApprove 호출 시 예외")
        void llmApprove_FromLlmApproved_ShouldThrow() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // when & then
            assertThatThrownBy(() -> feedback.llmApprove(ReviewNotes.empty(), NOW))
                    .isInstanceOf(InvalidFeedbackStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("llmReject 상태 전이")
    class LlmRejectTest {

        @Test
        @DisplayName("PENDING → LLM_REJECTED 전이 성공")
        void llmReject_FromPending_ShouldSucceed() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.pendingSafeFeedback();
            feedback.assignId(FeedbackQueueId.of(1L));

            // when
            feedback.llmReject(ReviewNotes.of("Invalid format"), NOW.plusSeconds(60));

            // then
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.LLM_REJECTED);
            assertThat(feedback.isTerminal()).isTrue();
        }
    }

    @Nested
    @DisplayName("humanApprove 상태 전이")
    class HumanApproveTest {

        @Test
        @DisplayName("LLM_APPROVED + MEDIUM → HUMAN_APPROVED 전이 성공")
        void humanApprove_FromLlmApprovedMedium_ShouldSucceed() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedMediumFeedback();

            // when
            feedback.humanApprove(ReviewNotes.of("Approved"), NOW.plusSeconds(120));

            // then
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.HUMAN_APPROVED);
        }

        @Test
        @DisplayName("LLM_APPROVED + SAFE에서 humanApprove 호출 시 예외 (불필요)")
        void humanApprove_FromLlmApprovedSafe_ShouldThrow() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // when & then
            assertThatThrownBy(() -> feedback.humanApprove(ReviewNotes.empty(), NOW))
                    .isInstanceOf(InvalidFeedbackStatusTransitionException.class)
                    .hasMessageContaining("SAFE risk level");
        }
    }

    @Nested
    @DisplayName("humanReject 상태 전이")
    class HumanRejectTest {

        @Test
        @DisplayName("LLM_APPROVED + MEDIUM → HUMAN_REJECTED 전이 성공")
        void humanReject_FromLlmApprovedMedium_ShouldSucceed() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedMediumFeedback();

            // when
            feedback.humanReject(ReviewNotes.of("Security concern"), NOW.plusSeconds(120));

            // then
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.HUMAN_REJECTED);
            assertThat(feedback.isTerminal()).isTrue();
        }
    }

    @Nested
    @DisplayName("merge 상태 전이")
    class MergeTest {

        @Test
        @DisplayName("LLM_APPROVED + SAFE → MERGED 전이 성공 (자동 병합)")
        void merge_FromLlmApprovedSafe_ShouldSucceed() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // when
            feedback.merge(NOW.plusSeconds(120));

            // then
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.MERGED);
            assertThat(feedback.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("LLM_APPROVED + MEDIUM에서 바로 merge 호출 시 예외")
        void merge_FromLlmApprovedMedium_ShouldThrow() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedMediumFeedback();

            // when & then
            assertThatThrownBy(() -> feedback.merge(NOW))
                    .isInstanceOf(InvalidFeedbackStatusTransitionException.class)
                    .hasMessageContaining("requires human approval");
        }

        @Test
        @DisplayName("HUMAN_APPROVED → MERGED 전이 성공")
        void merge_FromHumanApproved_ShouldSucceed() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.humanApprovedFeedback();

            // when
            feedback.merge(NOW.plusSeconds(180));

            // then
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.MERGED);
            assertThat(feedback.isTerminal()).isTrue();
        }
    }

    @Nested
    @DisplayName("쿼리 메서드")
    class QueryMethodsTest {

        @Test
        @DisplayName("canAutoMerge - SAFE + LLM_APPROVED일 때 true")
        void canAutoMerge_SafeAndLlmApproved_ShouldReturnTrue() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // then
            assertThat(feedback.canAutoMerge()).isTrue();
        }

        @Test
        @DisplayName("canAutoMerge - MEDIUM + LLM_APPROVED일 때 false")
        void canAutoMerge_MediumAndLlmApproved_ShouldReturnFalse() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedMediumFeedback();

            // then
            assertThat(feedback.canAutoMerge()).isFalse();
        }

        @Test
        @DisplayName("requiresHumanReview - MEDIUM + LLM_APPROVED일 때 true")
        void requiresHumanReview_MediumAndLlmApproved_ShouldReturnTrue() {
            // given
            FeedbackQueue feedback = FeedbackQueueFixture.llmApprovedMediumFeedback();

            // then
            assertThat(feedback.requiresHumanReview()).isTrue();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_ShouldRestoreCorrectly() {
            // when
            FeedbackQueue feedback =
                    FeedbackQueue.reconstitute(
                            FeedbackQueueId.of(100L),
                            FeedbackTargetType.CODING_RULE,
                            50L,
                            FeedbackType.MODIFY,
                            FeedbackPayload.of("{\"data\": \"test\"}"),
                            FeedbackStatus.LLM_APPROVED,
                            RiskLevel.MEDIUM,
                            ReviewNotes.of("Approved"),
                            NOW,
                            NOW.plusSeconds(60));

            // then
            assertThat(feedback.idValue()).isEqualTo(100L);
            assertThat(feedback.targetType()).isEqualTo(FeedbackTargetType.CODING_RULE);
            assertThat(feedback.targetId()).isEqualTo(50L);
            assertThat(feedback.status()).isEqualTo(FeedbackStatus.LLM_APPROVED);
            assertThat(feedback.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
            assertThat(feedback.isNew()).isFalse();
        }
    }
}
