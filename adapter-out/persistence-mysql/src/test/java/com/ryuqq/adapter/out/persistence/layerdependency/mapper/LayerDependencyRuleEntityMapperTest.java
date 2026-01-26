package com.ryuqq.adapter.out.persistence.layerdependency.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerDependencyRuleEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("LayerDependencyRuleEntityMapper 단위 테스트")
class LayerDependencyRuleEntityMapperTest extends MapperTestSupport {

    private LayerDependencyRuleEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LayerDependencyRuleEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerDependencyRuleJpaEntity entity = createTestEntity(now);

            // When
            LayerDependencyRule domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.architectureIdValue()).isEqualTo(100L);
            assertThat(domain.fromLayer()).isEqualTo(LayerType.DOMAIN);
            assertThat(domain.toLayer()).isEqualTo(LayerType.APPLICATION);
            assertThat(domain.dependencyType()).isEqualTo(DependencyType.ALLOWED);
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            LayerDependencyRuleJpaEntity entity = null;

            // When
            LayerDependencyRule domain = mapper.toDomain(entity);

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
            LayerDependencyRule domain = createTestDomain(now);

            // When
            LayerDependencyRuleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getArchitectureId()).isEqualTo(100L);
            assertThat(entity.getFromLayer()).isEqualTo("DOMAIN");
            assertThat(entity.getToLayer()).isEqualTo("APPLICATION");
            assertThat(entity.getDependencyType()).isEqualTo("ALLOWED");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            LayerDependencyRule domain = null;

            // When
            LayerDependencyRuleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private LayerDependencyRuleJpaEntity createTestEntity(Instant now) {
        return LayerDependencyRuleJpaEntity.of(
                1L, 100L, "DOMAIN", "APPLICATION", "ALLOWED", "Test Condition", now, now, null);
    }

    private LayerDependencyRule createTestDomain(Instant now) {
        return LayerDependencyRule.reconstitute(
                LayerDependencyRuleId.of(1L),
                ArchitectureId.of(100L),
                LayerType.DOMAIN,
                LayerType.APPLICATION,
                DependencyType.ALLOWED,
                ConditionDescription.of("Test Condition"),
                DeletionStatus.active(),
                now,
                now);
    }
}
