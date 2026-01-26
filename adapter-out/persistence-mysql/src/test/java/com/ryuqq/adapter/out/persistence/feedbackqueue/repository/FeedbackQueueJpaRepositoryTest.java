package com.ryuqq.adapter.out.persistence.feedbackqueue.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FeedbackQueueJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("FeedbackQueueJpaRepository 통합 테스트")
class FeedbackQueueJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private FeedbackQueueJpaRepository feedbackQueueJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - FeedbackQueue 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            FeedbackQueueJpaEntity entity =
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

            // When
            FeedbackQueueJpaEntity saved = feedbackQueueJpaRepository.save(entity);
            flushAndClear();

            // Then
            FeedbackQueueJpaEntity found =
                    feedbackQueueJpaRepository.findById(saved.getId()).orElseThrow();

            assertThat(found.getId()).isNotNull();
            assertThat(found.getTargetType()).isEqualTo(FeedbackTargetType.CODING_RULE);
            assertThat(found.getTargetId()).isEqualTo(100L);
            assertThat(found.getFeedbackType()).isEqualTo(FeedbackType.ADD);
            assertThat(found.getStatus()).isEqualTo(FeedbackStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 FeedbackQueue 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            FeedbackQueueJpaEntity entity =
                    FeedbackQueueJpaEntity.ofInstant(
                            null,
                            FeedbackTargetType.RULE_EXAMPLE,
                            null,
                            FeedbackType.ADD,
                            RiskLevel.SAFE,
                            "{\"example\": \"test\"}",
                            FeedbackStatus.PENDING,
                            null,
                            now,
                            now);

            FeedbackQueueJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<FeedbackQueueJpaEntity> result = feedbackQueueJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getTargetType()).isEqualTo(FeedbackTargetType.RULE_EXAMPLE);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void notFound() {
            // Given
            Long nonExistentId = 999L;

            // When
            Optional<FeedbackQueueJpaEntity> result =
                    feedbackQueueJpaRepository.findById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
