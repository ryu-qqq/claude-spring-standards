package com.ryuqq.adapter.out.persistence.feedbackqueue.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FeedbackQueueQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("FeedbackQueueQueryDslRepository 통합 테스트")
class FeedbackQueueQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private FeedbackQueueQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();

        // PENDING 상태 - SAFE
        FeedbackQueueJpaEntity feedback1 =
                FeedbackQueueJpaEntity.ofInstant(
                        null,
                        FeedbackTargetType.RULE_EXAMPLE,
                        null,
                        FeedbackType.ADD,
                        RiskLevel.SAFE,
                        "{\"example\": \"test1\"}",
                        FeedbackStatus.PENDING,
                        null,
                        now,
                        now);

        // PENDING 상태 - MEDIUM
        FeedbackQueueJpaEntity feedback2 =
                FeedbackQueueJpaEntity.ofInstant(
                        null,
                        FeedbackTargetType.CODING_RULE,
                        100L,
                        FeedbackType.ADD,
                        RiskLevel.MEDIUM,
                        "{\"rule\": \"new-rule\"}",
                        FeedbackStatus.PENDING,
                        null,
                        now,
                        now);

        // LLM_APPROVED 상태 - SAFE (자동 병합 가능)
        FeedbackQueueJpaEntity feedback3 =
                FeedbackQueueJpaEntity.ofInstant(
                        null,
                        FeedbackTargetType.CLASS_TEMPLATE,
                        50L,
                        FeedbackType.MODIFY,
                        RiskLevel.SAFE,
                        "{\"template\": \"updated\"}",
                        FeedbackStatus.LLM_APPROVED,
                        "Approved by LLM",
                        now,
                        now);

        // LLM_APPROVED 상태 - MEDIUM (사람 승인 필요)
        FeedbackQueueJpaEntity feedback4 =
                FeedbackQueueJpaEntity.ofInstant(
                        null,
                        FeedbackTargetType.CODING_RULE,
                        200L,
                        FeedbackType.MODIFY,
                        RiskLevel.MEDIUM,
                        "{\"rule\": \"modified\"}",
                        FeedbackStatus.LLM_APPROVED,
                        "Needs human review",
                        now,
                        now);

        // MERGED 상태
        FeedbackQueueJpaEntity feedback5 =
                FeedbackQueueJpaEntity.ofInstant(
                        null,
                        FeedbackTargetType.RULE_EXAMPLE,
                        10L,
                        FeedbackType.DELETE,
                        RiskLevel.SAFE,
                        "{\"reason\": \"obsolete\"}",
                        FeedbackStatus.MERGED,
                        "Merged successfully",
                        now,
                        now);

        persistAll(feedback1, feedback2, feedback3, feedback4, feedback5);
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 FeedbackQueue 조회")
        void success() {
            // Given
            List<FeedbackQueueJpaEntity> all =
                    queryDslRepository.findByStatus(FeedbackStatus.PENDING);
            Long id = all.get(0).getId();

            // When
            Optional<FeedbackQueueJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID")
        void notFound() {
            // When
            Optional<FeedbackQueueJpaEntity> result = queryDslRepository.findById(999L);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByStatus()")
    class FindByStatus {

        @Test
        @DisplayName("성공 - PENDING 상태 조회")
        void findPending() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findByStatus(FeedbackStatus.PENDING);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == FeedbackStatus.PENDING);
        }

        @Test
        @DisplayName("성공 - LLM_APPROVED 상태 조회")
        void findLlmApproved() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findByStatus(FeedbackStatus.LLM_APPROVED);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == FeedbackStatus.LLM_APPROVED);
        }
    }

    @Nested
    @DisplayName("findByTargetType()")
    class FindByTargetType {

        @Test
        @DisplayName("성공 - CODING_RULE 타입 조회")
        void findCodingRule() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findByTargetType(FeedbackTargetType.CODING_RULE);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getTargetType() == FeedbackTargetType.CODING_RULE);
        }
    }

    @Nested
    @DisplayName("findByRiskLevel()")
    class FindByRiskLevel {

        @Test
        @DisplayName("성공 - SAFE 위험도 조회")
        void findSafe() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findByRiskLevel(RiskLevel.SAFE);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(e -> e.getRiskLevel() == RiskLevel.SAFE);
        }

        @Test
        @DisplayName("성공 - MEDIUM 위험도 조회")
        void findMedium() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findByRiskLevel(RiskLevel.MEDIUM);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getRiskLevel() == RiskLevel.MEDIUM);
        }
    }

    @Nested
    @DisplayName("findPendingFeedbacks()")
    class FindPendingFeedbacks {

        @Test
        @DisplayName("성공 - 대기 중인 피드백 목록 조회")
        void success() {
            // When
            List<FeedbackQueueJpaEntity> result = queryDslRepository.findPendingFeedbacks();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == FeedbackStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("findAutoMergeableFeedbacks()")
    class FindAutoMergeableFeedbacks {

        @Test
        @DisplayName("성공 - 자동 병합 가능한 피드백 조회 (LLM_APPROVED + SAFE)")
        void success() {
            // When
            List<FeedbackQueueJpaEntity> result = queryDslRepository.findAutoMergeableFeedbacks();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(FeedbackStatus.LLM_APPROVED);
            assertThat(result.get(0).getRiskLevel()).isEqualTo(RiskLevel.SAFE);
        }
    }

    @Nested
    @DisplayName("findHumanReviewRequiredFeedbacks()")
    class FindHumanReviewRequiredFeedbacks {

        @Test
        @DisplayName("성공 - 사람 승인 필요한 피드백 조회 (LLM_APPROVED + MEDIUM)")
        void success() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findHumanReviewRequiredFeedbacks();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(FeedbackStatus.LLM_APPROVED);
            assertThat(result.get(0).getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }
    }

    @Nested
    @DisplayName("findByTarget()")
    class FindByTarget {

        @Test
        @DisplayName("성공 - 특정 대상에 대한 피드백 조회")
        void success() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findByTarget(FeedbackTargetType.CODING_RULE, 100L);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTargetType()).isEqualTo(FeedbackTargetType.CODING_RULE);
            assertThat(result.get(0).getTargetId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findBySlice()")
    class FindBySlice {

        @Test
        @DisplayName("성공 - 커서 없이 첫 페이지 조회")
        void firstPage() {
            // When
            List<FeedbackQueueJpaEntity> result = queryDslRepository.findBySlice(null, null, 3);

            // Then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("성공 - 상태 필터와 함께 조회")
        void withStatusFilter() {
            // When
            List<FeedbackQueueJpaEntity> result =
                    queryDslRepository.findBySlice(FeedbackStatus.PENDING, null, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == FeedbackStatus.PENDING);
        }

        @Test
        @DisplayName("성공 - 커서 기반 조회")
        void withCursor() {
            // Given
            List<FeedbackQueueJpaEntity> all = queryDslRepository.findBySlice(null, null, 10);
            Long cursor = all.get(0).getId();

            // When
            List<FeedbackQueueJpaEntity> result = queryDslRepository.findBySlice(null, cursor, 10);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getId() < cursor);
        }
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID")
        void exists() {
            // Given
            List<FeedbackQueueJpaEntity> all =
                    queryDslRepository.findByStatus(FeedbackStatus.PENDING);
            Long id = all.get(0).getId();

            // When
            boolean result = queryDslRepository.existsById(id);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID")
        void notExists() {
            // When
            boolean result = queryDslRepository.existsById(999L);

            // Then
            assertThat(result).isFalse();
        }
    }
}
