package com.ryuqq.adapter.out.persistence.classtemplate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ClassTemplateJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ClassTemplateJpaEntityMapper 단위 테스트")
class ClassTemplateJpaEntityMapperTest extends MapperTestSupport {

    private ClassTemplateJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClassTemplateJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ClassTemplateJpaEntity entity = createTestEntity(now);

            // When
            ClassTemplate domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.structureIdValue()).isEqualTo(100L);
            assertThat(domain.classTypeIdValue()).isEqualTo(1L);
            assertThat(domain.templateCodeValue()).isEqualTo("public class Test { }");
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            ClassTemplateJpaEntity entity = null;

            // When
            ClassTemplate domain = mapper.toDomain(entity);

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
            ClassTemplate domain = createTestDomain(now);

            // When
            ClassTemplateJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getStructureId()).isEqualTo(100L);
            assertThat(entity.getClassTypeId()).isEqualTo(1L);
            assertThat(entity.getTemplateCode()).isEqualTo("public class Test { }");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            ClassTemplate domain = null;

            // When
            ClassTemplateJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private ClassTemplateJpaEntity createTestEntity(Instant now) {
        return ClassTemplateJpaEntity.of(
                1L,
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

    private ClassTemplate createTestDomain(Instant now) {
        return ClassTemplate.of(
                ClassTemplateId.of(1L),
                PackageStructureId.of(100L),
                ClassTypeId.of(1L), // AGGREGATE
                TemplateCode.of("public class Test { }"),
                NamingPattern.of("Test.*"),
                TemplateDescription.of("Test Description"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                DeletionStatus.active(),
                now,
                now);
    }
}
