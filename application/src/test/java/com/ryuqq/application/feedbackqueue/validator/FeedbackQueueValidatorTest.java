package com.ryuqq.application.feedbackqueue.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.feedbackqueue.manager.FeedbackQueueReadManager;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackStatusTransitionException;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueueValidator 단위 테스트
 *
 * <p>FeedbackQueue 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("FeedbackQueueValidator 단위 테스트")
class FeedbackQueueValidatorTest {

    @Mock private FeedbackQueueReadManager feedbackQueueReadManager;

    private FeedbackQueueValidator sut;

    @BeforeEach
    void setUp() {
        sut = new FeedbackQueueValidator(feedbackQueueReadManager);
    }

    @Nested
    @DisplayName("getAndValidateForProcess 메서드")
    class GetAndValidateForProcess {

        @Nested
        @DisplayName("LLM_APPROVE 액션")
        class LlmApprove {

            @Test
            @DisplayName("성공 - PENDING 상태에서 LLM_APPROVE 가능")
            void getAndValidateForProcess_WhenPendingAndLlmApprove_ShouldReturnFeedback() {
                // given
                Long feedbackId = 1L;
                FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
                feedbackQueue.assignId(FeedbackQueueId.of(feedbackId));

                given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                        .willReturn(feedbackQueue);

                // when
                FeedbackQueue result =
                        sut.getAndValidateForProcess(feedbackId, FeedbackAction.LLM_APPROVE);

                // then
                assertThat(result).isEqualTo(feedbackQueue);
            }

            @Test
            @DisplayName("실패 - PENDING이 아닌 상태에서 LLM_APPROVE 불가")
            void getAndValidateForProcess_WhenNotPendingAndLlmApprove_ShouldThrow() {
                // given
                Long feedbackId = 1L;
                FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();

                given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                        .willReturn(feedbackQueue);

                // when & then
                assertThatThrownBy(
                                () ->
                                        sut.getAndValidateForProcess(
                                                feedbackId, FeedbackAction.LLM_APPROVE))
                        .isInstanceOf(InvalidFeedbackStatusTransitionException.class);
            }
        }

        @Nested
        @DisplayName("LLM_REJECT 액션")
        class LlmReject {

            @Test
            @DisplayName("성공 - PENDING 상태에서 LLM_REJECT 가능")
            void getAndValidateForProcess_WhenPendingAndLlmReject_ShouldReturnFeedback() {
                // given
                Long feedbackId = 1L;
                FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
                feedbackQueue.assignId(FeedbackQueueId.of(feedbackId));

                given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                        .willReturn(feedbackQueue);

                // when
                FeedbackQueue result =
                        sut.getAndValidateForProcess(feedbackId, FeedbackAction.LLM_REJECT);

                // then
                assertThat(result).isEqualTo(feedbackQueue);
            }
        }

        @Nested
        @DisplayName("HUMAN_APPROVE 액션")
        class HumanApprove {

            @Test
            @DisplayName("성공 - LLM_APPROVED + MEDIUM 리스크 상태에서 HUMAN_APPROVE 가능")
            void
                    getAndValidateForProcess_WhenLlmApprovedMediumAndHumanApprove_ShouldReturnFeedback() {
                // given
                Long feedbackId = 2L;
                FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedMediumFeedback();

                given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                        .willReturn(feedbackQueue);

                // when
                FeedbackQueue result =
                        sut.getAndValidateForProcess(feedbackId, FeedbackAction.HUMAN_APPROVE);

                // then
                assertThat(result).isEqualTo(feedbackQueue);
            }

            @Test
            @DisplayName("실패 - SAFE 리스크에서는 HUMAN_APPROVE 불가")
            void getAndValidateForProcess_WhenLlmApprovedSafeAndHumanApprove_ShouldThrow() {
                // given
                Long feedbackId = 1L;
                FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();

                given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                        .willReturn(feedbackQueue);

                // when & then
                assertThatThrownBy(
                                () ->
                                        sut.getAndValidateForProcess(
                                                feedbackId, FeedbackAction.HUMAN_APPROVE))
                        .isInstanceOf(InvalidFeedbackStatusTransitionException.class);
            }
        }

        @Nested
        @DisplayName("HUMAN_REJECT 액션")
        class HumanReject {

            @Test
            @DisplayName("성공 - LLM_APPROVED + MEDIUM 리스크 상태에서 HUMAN_REJECT 가능")
            void
                    getAndValidateForProcess_WhenLlmApprovedMediumAndHumanReject_ShouldReturnFeedback() {
                // given
                Long feedbackId = 2L;
                FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedMediumFeedback();

                given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                        .willReturn(feedbackQueue);

                // when
                FeedbackQueue result =
                        sut.getAndValidateForProcess(feedbackId, FeedbackAction.HUMAN_REJECT);

                // then
                assertThat(result).isEqualTo(feedbackQueue);
            }
        }
    }

    @Nested
    @DisplayName("getAndValidateForMerge 메서드")
    class GetAndValidateForMerge {

        @Test
        @DisplayName("성공 - LLM_APPROVED + SAFE 리스크에서 머지 가능")
        void getAndValidateForMerge_WhenLlmApprovedSafe_ShouldReturnFeedback() {
            // given
            Long feedbackId = 1L;
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();

            given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                    .willReturn(feedbackQueue);

            // when
            FeedbackQueue result = sut.getAndValidateForMerge(feedbackId);

            // then
            assertThat(result).isEqualTo(feedbackQueue);
        }

        @Test
        @DisplayName("성공 - HUMAN_APPROVED + MEDIUM 리스크에서 머지 가능")
        void getAndValidateForMerge_WhenHumanApprovedMedium_ShouldReturnFeedback() {
            // given
            Long feedbackId = 2L;
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.humanApprovedFeedback();

            given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                    .willReturn(feedbackQueue);

            // when
            FeedbackQueue result = sut.getAndValidateForMerge(feedbackId);

            // then
            assertThat(result).isEqualTo(feedbackQueue);
        }

        @Test
        @DisplayName("실패 - PENDING 상태에서는 머지 불가")
        void getAndValidateForMerge_WhenPending_ShouldThrow() {
            // given
            Long feedbackId = 1L;
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
            feedbackQueue.assignId(FeedbackQueueId.of(feedbackId));

            given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                    .willReturn(feedbackQueue);

            // when & then
            assertThatThrownBy(() -> sut.getAndValidateForMerge(feedbackId))
                    .isInstanceOf(InvalidFeedbackStatusTransitionException.class);
        }

        @Test
        @DisplayName("실패 - LLM_APPROVED + MEDIUM 리스크에서는 HUMAN 승인 필요")
        void getAndValidateForMerge_WhenLlmApprovedMedium_ShouldThrow() {
            // given
            Long feedbackId = 2L;
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedMediumFeedback();

            given(feedbackQueueReadManager.getById(FeedbackQueueId.of(feedbackId)))
                    .willReturn(feedbackQueue);

            // when & then
            assertThatThrownBy(() -> sut.getAndValidateForMerge(feedbackId))
                    .isInstanceOf(InvalidFeedbackStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 피드백 큐")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(1L);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
            feedbackQueue.assignId(id);

            given(feedbackQueueReadManager.getById(id)).willReturn(feedbackQueue);

            // when & then - no exception
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("validateCanMerge 메서드")
    class ValidateCanMerge {

        @Test
        @DisplayName("성공 - SAFE 리스크 + LLM_APPROVED")
        void validateCanMerge_WhenSafeAndLlmApproved_ShouldNotThrow() {
            // given
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // when & then - no exception
            assertThatCode(() -> sut.validateCanMerge(feedbackQueue)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("성공 - MEDIUM 리스크 + HUMAN_APPROVED")
        void validateCanMerge_WhenMediumAndHumanApproved_ShouldNotThrow() {
            // given
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.humanApprovedFeedback();

            // when & then - no exception
            assertThatCode(() -> sut.validateCanMerge(feedbackQueue)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - PENDING 상태")
        void validateCanMerge_WhenPending_ShouldThrow() {
            // given
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
            feedbackQueue.assignId(FeedbackQueueId.of(1L));

            // when & then
            assertThatThrownBy(() -> sut.validateCanMerge(feedbackQueue))
                    .isInstanceOf(InvalidFeedbackStatusTransitionException.class);
        }
    }
}
