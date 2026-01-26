package com.ryuqq.adapter.out.persistence.resourcetemplate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ResourceTemplateJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ResourceTemplateJpaEntityMapper 단위 테스트")
class ResourceTemplateJpaEntityMapperTest extends MapperTestSupport {

    private ResourceTemplateJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ResourceTemplateJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ResourceTemplateJpaEntity entity = createTestEntity(now);

            // When
            ResourceTemplate domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.moduleIdValue()).isEqualTo(100L);
            assertThat(domain.category()).isEqualTo(TemplateCategory.CONFIG);
            assertThat(domain.filePathValue()).isEqualTo("application.yml");
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // When
            ResourceTemplate domain = mapper.toDomain(null);

            // Then
            assertThat(domain).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("성공 - Domain을 Entity로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ResourceTemplate domain = createTestDomain(now);

            // When
            ResourceTemplateJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getModuleId()).isEqualTo(100L);
            assertThat(entity.getCategory()).isEqualTo("CONFIG");
            assertThat(entity.getFilePath()).isEqualTo("application.yml");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // When
            ResourceTemplateJpaEntity entity = mapper.toEntity(null);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private ResourceTemplateJpaEntity createTestEntity(Instant now) {
        return ResourceTemplateJpaEntity.of(
                1L,
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

    private ResourceTemplate createTestDomain(Instant now) {
        return ResourceTemplate.reconstitute(
                ResourceTemplateId.of(1L),
                ModuleId.of(100L),
                TemplateCategory.CONFIG,
                TemplatePath.of("application.yml"),
                FileType.YAML,
                "Test Description",
                TemplateContent.of("template content"),
                true,
                DeletionStatus.active(),
                now,
                now);
    }
}
