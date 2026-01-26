package com.ryuqq.adapter.out.persistence.techstack.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TechStackJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("TechStackJpaRepository 통합 테스트")
class TechStackJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private TechStackJpaRepository techStackJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - TechStack 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            TechStackJpaEntity entity = createTestEntity(now);

            // When
            TechStackJpaEntity saved = techStackJpaRepository.save(entity);
            flushAndClear();

            // Then
            TechStackJpaEntity found = techStackJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getName()).isEqualTo("Spring Boot");
            assertThat(found.getStatus()).isEqualTo("ACTIVE");
            assertThat(found.getLanguageType()).isEqualTo("JAVA");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 TechStack 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            TechStackJpaEntity entity = createTestEntity(now);
            TechStackJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<TechStackJpaEntity> result = techStackJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private TechStackJpaEntity createTestEntity(Instant now) {
        return TechStackJpaEntity.of(
                null,
                "Spring Boot",
                "ACTIVE",
                "JAVA",
                "21",
                "[]",
                "SPRING_BOOT",
                "3.5.0",
                "[]",
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                "[]",
                now,
                now,
                null);
    }
}
