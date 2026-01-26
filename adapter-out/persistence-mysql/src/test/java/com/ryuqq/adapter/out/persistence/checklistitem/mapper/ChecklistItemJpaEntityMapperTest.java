package com.ryuqq.adapter.out.persistence.checklistitem.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.ChecklistSource;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChecklistItemJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ChecklistItemJpaEntityMapper 단위 테스트")
class ChecklistItemJpaEntityMapperTest extends MapperTestSupport {

    private ChecklistItemJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ChecklistItemJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ChecklistItemJpaEntity entity = createTestEntity(now);

            // When
            ChecklistItem domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.ruleIdValue()).isEqualTo(100L);
            assertThat(domain.sequenceOrderValue()).isEqualTo(1);
            assertThat(domain.checkDescriptionValue()).isEqualTo("Test Check Description");
            assertThat(domain.checkType()).isEqualTo(CheckType.AUTOMATED);
            assertThat(domain.isCritical()).isFalse();
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            ChecklistItemJpaEntity entity = null;

            // When
            ChecklistItem domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("성공 - Domain을 Entity로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ChecklistItem domain = createTestDomain(now);

            // When
            ChecklistItemJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getRuleId()).isEqualTo(100L);
            assertThat(entity.getSequenceOrder()).isEqualTo(1);
            assertThat(entity.getCheckDescription()).isEqualTo("Test Check Description");
            assertThat(entity.getCheckType()).isEqualTo("AUTOMATED");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            ChecklistItem domain = null;

            // When
            ChecklistItemJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private ChecklistItemJpaEntity createTestEntity(Instant now) {
        return ChecklistItemJpaEntity.of(
                1L,
                100L,
                1,
                "Test Check Description",
                "AUTOMATED",
                "SONARLINT",
                "RULE-001",
                false,
                "MANUAL",
                null,
                now,
                now,
                null);
    }

    private ChecklistItem createTestDomain(Instant now) {
        return ChecklistItem.reconstitute(
                ChecklistItemId.of(1L),
                CodingRuleId.of(100L),
                SequenceOrder.of(1),
                com.ryuqq.domain.checklistitem.vo.CheckDescription.of("Test Check Description"),
                CheckType.AUTOMATED,
                com.ryuqq.domain.checklistitem.vo.AutomationTool.SONARLINT,
                com.ryuqq.domain.checklistitem.vo.AutomationRuleId.of("RULE-001"),
                false,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }
}
