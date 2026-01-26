package com.ryuqq.adapter.out.persistence.resourcetemplate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ResourceTemplateJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ResourceTemplateJpaRepository 통합 테스트")
class ResourceTemplateJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private ResourceTemplateJpaRepository resourceTemplateJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - ResourceTemplate 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ResourceTemplateJpaEntity entity = createTestEntity(now);

            // When
            ResourceTemplateJpaEntity saved = resourceTemplateJpaRepository.save(entity);
            flushAndClear();

            // Then
            ResourceTemplateJpaEntity found =
                    resourceTemplateJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getModuleId()).isEqualTo(100L);
            assertThat(found.getCategory()).isEqualTo("CONFIG");
            assertThat(found.getFilePath()).isEqualTo("application.yml");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ResourceTemplate 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ResourceTemplateJpaEntity entity = createTestEntity(now);
            ResourceTemplateJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ResourceTemplateJpaEntity> result = resourceTemplateJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private ResourceTemplateJpaEntity createTestEntity(Instant now) {
        return ResourceTemplateJpaEntity.of(
                null,
                100L,
                "CONFIG",
                "application.yml",
                "YAML",
                "Test Description",
                "template content",
                true,
                now,
                now,
                null);
    }
}
