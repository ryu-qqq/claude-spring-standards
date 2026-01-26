package com.ryuqq.adapter.out.persistence.module.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ModuleJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ModuleJpaRepository 통합 테스트")
class ModuleJpaRepositoryTest extends RepositoryTestSupport {

    private static final Long DEFAULT_LAYER_ID = 100L;

    @Autowired private ModuleJpaRepository moduleJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - Module 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ModuleJpaEntity entity = createTestEntity(now);

            // When
            ModuleJpaEntity saved = moduleJpaRepository.save(entity);
            flushAndClear();

            // Then
            ModuleJpaEntity found = moduleJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getLayerId()).isEqualTo(DEFAULT_LAYER_ID);
            assertThat(found.getName()).isEqualTo("Test Module");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Module 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ModuleJpaEntity entity = createTestEntity(now);
            ModuleJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ModuleJpaEntity> result = moduleJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private ModuleJpaEntity createTestEntity(Instant now) {
        return ModuleJpaEntity.of(
                null,
                DEFAULT_LAYER_ID,
                null,
                "Test Module",
                "Test Description",
                "test-module",
                ":test-module",
                now,
                now,
                null);
    }
}
