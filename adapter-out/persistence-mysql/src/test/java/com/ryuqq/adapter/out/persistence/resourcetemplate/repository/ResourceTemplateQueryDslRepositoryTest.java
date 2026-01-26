package com.ryuqq.adapter.out.persistence.resourcetemplate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
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
 * ResourceTemplateQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ResourceTemplateQueryDslRepository 통합 테스트")
class ResourceTemplateQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ResourceTemplateQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ResourceTemplateJpaEntity.of(
                        null,
                        100L,
                        "CONFIG",
                        "application.yml",
                        "YAML",
                        "Desc 1",
                        "content1",
                        true,
                        now,
                        now,
                        null),
                ResourceTemplateJpaEntity.of(
                        null,
                        100L,
                        "SCRIPT",
                        "script.sh",
                        "SHELL",
                        "Desc 2",
                        "content2",
                        false,
                        now,
                        now,
                        null),
                ResourceTemplateJpaEntity.of(
                        null,
                        200L,
                        "CONFIG",
                        "application.yml",
                        "YAML",
                        "Desc 3",
                        "content3",
                        true,
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ResourceTemplate 조회")
        void success() {
            // Given
            List<ResourceTemplateJpaEntity> all = queryDslRepository.findByModuleId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<ResourceTemplateJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getModuleId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findByModuleId()")
    class FindByModuleId {

        @Test
        @DisplayName("성공 - ModuleId로 ResourceTemplate 목록 조회")
        void success() {
            // When
            List<ResourceTemplateJpaEntity> result = queryDslRepository.findByModuleId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getModuleId().equals(100L));
        }
    }
}
