package com.ryuqq.adapter.out.persistence.layerdependency.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LayerDependencyRuleJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("LayerDependencyRuleJpaRepository 통합 테스트")
class LayerDependencyRuleJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private LayerDependencyRuleJpaRepository layerDependencyRuleJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - LayerDependencyRule 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerDependencyRuleJpaEntity entity = createTestEntity(now);

            // When
            LayerDependencyRuleJpaEntity saved = layerDependencyRuleJpaRepository.save(entity);
            flushAndClear();

            // Then
            LayerDependencyRuleJpaEntity found =
                    layerDependencyRuleJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getArchitectureId()).isEqualTo(100L);
            assertThat(found.getFromLayer()).isEqualTo("DOMAIN");
            assertThat(found.getToLayer()).isEqualTo("APPLICATION");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 LayerDependencyRule 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerDependencyRuleJpaEntity entity = createTestEntity(now);
            LayerDependencyRuleJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<LayerDependencyRuleJpaEntity> result =
                    layerDependencyRuleJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private LayerDependencyRuleJpaEntity createTestEntity(Instant now) {
        return LayerDependencyRuleJpaEntity.of(
                null, 100L, "DOMAIN", "APPLICATION", "ALLOWED", "Test Condition", now, now, null);
    }
}
