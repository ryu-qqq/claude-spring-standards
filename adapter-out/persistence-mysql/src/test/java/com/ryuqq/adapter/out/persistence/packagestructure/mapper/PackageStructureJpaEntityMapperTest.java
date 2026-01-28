package com.ryuqq.adapter.out.persistence.packagestructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackageStructureJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("PackageStructureJpaEntityMapper 단위 테스트")
class PackageStructureJpaEntityMapperTest extends MapperTestSupport {

    private PackageStructureJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackageStructureJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            PackageStructureJpaEntity entity = createTestEntity(now);

            // When
            PackageStructure domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.moduleIdValue()).isEqualTo(100L);
            assertThat(domain.pathPatternValue()).isEqualTo("com.test");
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            PackageStructureJpaEntity entity = null;

            // When
            PackageStructure domain = mapper.toDomain(entity);

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
            PackageStructure domain = createTestDomain(now);

            // When
            PackageStructureJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getModuleId()).isEqualTo(100L);
            assertThat(entity.getPathPattern()).isEqualTo("com.test");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            PackageStructure domain = null;

            // When
            PackageStructureJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private PackageStructureJpaEntity createTestEntity(Instant now) {
        return PackageStructureJpaEntity.of(
                1L, 100L, "com.test", "Test Description", now, now, null);
    }

    private PackageStructure createTestDomain(Instant now) {
        return PackageStructure.reconstitute(
                PackageStructureId.of(1L),
                ModuleId.of(100L),
                PathPattern.of("com.test"),
                "Test Description",
                DeletionStatus.active(),
                now,
                now);
    }
}
