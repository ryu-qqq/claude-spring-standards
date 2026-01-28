package com.ryuqq.adapter.out.persistence.packagestructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PackageStructureJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("PackageStructureJpaRepository 통합 테스트")
class PackageStructureJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private PackageStructureJpaRepository packageStructureJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - PackageStructure 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            PackageStructureJpaEntity entity = createTestEntity(now);

            // When
            PackageStructureJpaEntity saved = packageStructureJpaRepository.save(entity);
            flushAndClear();

            // Then
            PackageStructureJpaEntity found =
                    packageStructureJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getModuleId()).isEqualTo(100L);
            assertThat(found.getPathPattern()).isEqualTo("com.test");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 PackageStructure 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            PackageStructureJpaEntity entity = createTestEntity(now);
            PackageStructureJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<PackageStructureJpaEntity> result = packageStructureJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private PackageStructureJpaEntity createTestEntity(Instant now) {
        return PackageStructureJpaEntity.of(
                null, 100L, "com.test", "Test Description", now, now, null);
    }
}
