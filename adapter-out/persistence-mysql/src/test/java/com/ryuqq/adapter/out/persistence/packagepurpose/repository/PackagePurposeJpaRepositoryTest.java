package com.ryuqq.adapter.out.persistence.packagepurpose.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PackagePurposeJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("PackagePurposeJpaRepository 통합 테스트")
class PackagePurposeJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private PackagePurposeJpaRepository packagePurposeJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - PackagePurpose 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            PackagePurposeJpaEntity entity = createTestEntity(now);

            // When
            PackagePurposeJpaEntity saved = packagePurposeJpaRepository.save(entity);
            flushAndClear();

            // Then
            PackagePurposeJpaEntity found =
                    packagePurposeJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getStructureId()).isEqualTo(1L);
            assertThat(found.getCode()).isEqualTo("DOMAIN");
            assertThat(found.getName()).isEqualTo("Domain Layer");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 PackagePurpose 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            PackagePurposeJpaEntity entity = createTestEntity(now);
            PackagePurposeJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<PackagePurposeJpaEntity> result = packagePurposeJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private PackagePurposeJpaEntity createTestEntity(Instant now) {
        return PackagePurposeJpaEntity.of(
                null, 1L, "DOMAIN", "Domain Layer", "Test Description", now, now, null);
    }
}
