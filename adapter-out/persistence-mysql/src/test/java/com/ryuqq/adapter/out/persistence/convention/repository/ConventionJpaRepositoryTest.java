package com.ryuqq.adapter.out.persistence.convention.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ConventionJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ConventionJpaRepository 통합 테스트")
class ConventionJpaRepositoryTest extends RepositoryTestSupport {

    private static final Long DEFAULT_MODULE_ID = 1L;

    @Autowired private ConventionJpaRepository conventionJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - Convention 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ConventionJpaEntity entity = createTestEntity(now);

            // When
            ConventionJpaEntity saved = conventionJpaRepository.save(entity);
            flushAndClear();

            // Then
            ConventionJpaEntity found =
                    conventionJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getModuleId()).isEqualTo(DEFAULT_MODULE_ID);
            assertThat(found.getVersion()).isEqualTo("1.0.0");
            assertThat(found.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Convention 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ConventionJpaEntity entity = createTestEntity(now);
            ConventionJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ConventionJpaEntity> result = conventionJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private ConventionJpaEntity createTestEntity(Instant now) {
        return ConventionJpaEntity.of(
                null, DEFAULT_MODULE_ID, "1.0.0", "Test Description", true, now, now, null);
    }
}
