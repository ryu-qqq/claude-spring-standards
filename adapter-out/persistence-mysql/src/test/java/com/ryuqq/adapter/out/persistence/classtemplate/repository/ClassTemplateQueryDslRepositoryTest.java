package com.ryuqq.adapter.out.persistence.classtemplate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassTemplateQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ClassTemplateQueryDslRepository 통합 테스트")
class ClassTemplateQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ClassTemplateQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ClassTemplateJpaEntity.of(
                        null,
                        100L,
                        1L, // classTypeId (AGGREGATE)
                        "public class Test1 { }",
                        "Test1.*",
                        "[]",
                        "[]",
                        "[]",
                        "[]",
                        "[]",
                        "Desc 1",
                        now,
                        now,
                        null),
                ClassTemplateJpaEntity.of(
                        null,
                        100L,
                        2L, // classTypeId (VALUE_OBJECT)
                        "public record Test2 { }",
                        "Test2.*",
                        "[]",
                        "[]",
                        "[]",
                        "[]",
                        "[]",
                        "Desc 2",
                        now,
                        now,
                        null),
                ClassTemplateJpaEntity.of(
                        null,
                        200L,
                        1L, // classTypeId (AGGREGATE)
                        "public class Test3 { }",
                        "Test3.*",
                        "[]",
                        "[]",
                        "[]",
                        "[]",
                        "[]",
                        "Desc 3",
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ClassTemplate 조회")
        void success() {
            // Given
            List<ClassTemplateJpaEntity> all = queryDslRepository.findBySlice(100L, null, 10);
            Long id = all.get(0).getId();

            // When
            Optional<ClassTemplateJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getStructureId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findBySlice()")
    class FindBySlice {

        @Test
        @DisplayName("성공 - StructureId로 ClassTemplate 목록 조회")
        void success() {
            // When
            List<ClassTemplateJpaEntity> result = queryDslRepository.findBySlice(100L, null, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStructureId().equals(100L));
        }
    }
}
