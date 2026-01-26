package com.ryuqq.adapter.out.persistence.classtemplate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassTemplateJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ClassTemplateJpaRepository 통합 테스트")
class ClassTemplateJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private ClassTemplateJpaRepository classTemplateJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - ClassTemplate 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ClassTemplateJpaEntity entity = createTestEntity(now);

            // When
            ClassTemplateJpaEntity saved = classTemplateJpaRepository.save(entity);
            flushAndClear();

            // Then
            ClassTemplateJpaEntity found =
                    classTemplateJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getStructureId()).isEqualTo(100L);
            assertThat(found.getClassTypeId()).isEqualTo(1L);
            assertThat(found.getTemplateCode()).isEqualTo("public class Test { }");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ClassTemplate 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ClassTemplateJpaEntity entity = createTestEntity(now);
            ClassTemplateJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ClassTemplateJpaEntity> result = classTemplateJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private ClassTemplateJpaEntity createTestEntity(Instant now) {
        return ClassTemplateJpaEntity.of(
                null,
                100L,
                1L, // classTypeId (AGGREGATE)
                "public class Test { }",
                "Test.*",
                "[]",
                "[]",
                "[]",
                "[]",
                "[]",
                "Test Description",
                now,
                now,
                null);
    }
}
