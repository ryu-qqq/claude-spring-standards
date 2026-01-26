package com.ryuqq.adapter.out.persistence.feedbackqueue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackPayload;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.ReviewNotes;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueueJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("FeedbackQueueJpaEntityMapper 단위 테스트")
class FeedbackQueueJpaEntityMapperTest extends MapperTestSupport {

    private FeedbackQueueJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FeedbackQueueJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            FeedbackQueueJpaEntity entity =
                    FeedbackQueueJpaEntity.ofInstant(
                            1L,
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
            FeedbackQueue domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.targetType()).isEqualTo(FeedbackTargetType.CODING_RULE);
            assertThat(domain.targetId()).isEqualTo(100L);
            assertThat(domain.feedbackType()).isEqualTo(FeedbackType.ADD);
            assertThat(domain.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
            assertThat(domain.payloadValue()).isEqualTo("{\"rule\": \"new-rule\"}");
            assertThat(domain.status()).isEqualTo(FeedbackStatus.PENDING);
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            FeedbackQueueJpaEntity entity = null;

            // When
            FeedbackQueue domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNull();
        }

        @Test
        @DisplayName("성공 - ReviewNotes 파싱 (값이 있는 경우)")
        void reviewNotesParsing() {
            // Given
            Instant now = Instant.now();
            FeedbackQueueJpaEntity entity =
                    FeedbackQueueJpaEntity.ofInstant(
                            1L,
                            FeedbackTargetType.RULE_EXAMPLE,
                            null,
                            FeedbackType.ADD,
                            RiskLevel.SAFE,
                            "{\"example\": \"test\"}",
                            FeedbackStatus.LLM_APPROVED,
                            "Approved by LLM",
                            now,
                            now);

            // When
            FeedbackQueue domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.reviewNotesValue()).isEqualTo("Approved by LLM");
        }

        @Test
        @DisplayName("성공 - ReviewNotes 빈 값 처리")
        void emptyReviewNotes() {
            // Given
            Instant now = Instant.now();
            FeedbackQueueJpaEntity entity =
                    FeedbackQueueJpaEntity.ofInstant(
                            1L,
                            FeedbackTargetType.CLASS_TEMPLATE,
                            50L,
                            FeedbackType.MODIFY,
                            RiskLevel.SAFE,
                            "{\"data\": \"test\"}",
                            FeedbackStatus.PENDING,
                            "",
                            now,
                            now);

            // When
            FeedbackQueue domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.reviewNotesValue()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("성공 - 신규 Domain을 Entity로 변환")
        void newDomain() {
            // Given
            FeedbackQueue domain = FeedbackQueueFixture.pendingSafeFeedback();

            // When
            FeedbackQueueJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isNull(); // 신규이므로 ID 없음
            assertThat(entity.getTargetType()).isEqualTo(FeedbackTargetType.RULE_EXAMPLE);
            assertThat(entity.getFeedbackType()).isEqualTo(FeedbackType.ADD);
            assertThat(entity.getStatus()).isEqualTo(FeedbackStatus.PENDING);
            assertThat(entity.getRiskLevel()).isEqualTo(RiskLevel.SAFE);
        }

        @Test
        @DisplayName("성공 - 기존 Domain을 Entity로 변환 (ID 유지)")
        void existingDomain() {
            // Given
            Instant now = Instant.now();
            FeedbackQueue domain =
                    FeedbackQueue.reconstitute(
                            FeedbackQueueId.of(1L),
                            FeedbackTargetType.CODING_RULE,
                            100L,
                            FeedbackType.MODIFY,
                            FeedbackPayload.of("{\"rule\": \"updated\"}"),
                            FeedbackStatus.LLM_APPROVED,
                            RiskLevel.MEDIUM,
                            ReviewNotes.of("Approved"),
                            now,
                            now);

            // When
            FeedbackQueueJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getTargetType()).isEqualTo(FeedbackTargetType.CODING_RULE);
            assertThat(entity.getTargetId()).isEqualTo(100L);
            assertThat(entity.getStatus()).isEqualTo(FeedbackStatus.LLM_APPROVED);
            assertThat(entity.getReviewNotes()).isEqualTo("Approved");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            FeedbackQueue domain = null;

            // When
            FeedbackQueueJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }
}
