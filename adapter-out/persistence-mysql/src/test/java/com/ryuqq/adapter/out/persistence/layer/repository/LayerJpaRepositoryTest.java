package com.ryuqq.adapter.out.persistence.layer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LayerJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("LayerJpaRepository 통합 테스트")
class LayerJpaRepositoryTest extends RepositoryTestSupport {

    private static final Long DEFAULT_ARCHITECTURE_ID = 1L;

    @Autowired private LayerJpaRepository layerJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - Layer 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerJpaEntity entity = createTestEntity(now);

            // When
            LayerJpaEntity saved = layerJpaRepository.save(entity);
            flushAndClear();

            // Then
            LayerJpaEntity found = layerJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getArchitectureId()).isEqualTo(DEFAULT_ARCHITECTURE_ID);
            assertThat(found.getCode()).isEqualTo("DOMAIN");
            assertThat(found.getName()).isEqualTo("Domain Layer");
            assertThat(found.getDescription()).isEqualTo("Test Description");
            assertThat(found.getOrderIndex()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Layer 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerJpaEntity entity = createTestEntity(now);
            LayerJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<LayerJpaEntity> result = layerJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void notFound() {
            // When
            Optional<LayerJpaEntity> result = layerJpaRepository.findById(999999L);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("성공 - Layer 삭제 (Hard Delete)")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerJpaEntity entity = createTestEntity(now);
            LayerJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            layerJpaRepository.deleteById(id);
            flushAndClear();

            // Then
            Optional<LayerJpaEntity> result = layerJpaRepository.findById(id);
            assertThat(result).isEmpty();
        }
    }

    // Helper method
    private LayerJpaEntity createTestEntity(Instant now) {
        return LayerJpaEntity.of(
                null,
                DEFAULT_ARCHITECTURE_ID,
                "DOMAIN",
                "Domain Layer",
                "Test Description",
                1,
                now,
                now,
                null);
    }
}
