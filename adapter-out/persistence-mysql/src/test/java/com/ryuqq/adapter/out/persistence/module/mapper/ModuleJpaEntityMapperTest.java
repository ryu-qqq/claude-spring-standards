package com.ryuqq.adapter.out.persistence.module.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModuleJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ModuleJpaEntityMapper 단위 테스트")
class ModuleJpaEntityMapperTest extends MapperTestSupport {

    private static final Long DEFAULT_LAYER_ID = 100L;

    private ModuleJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ModuleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ModuleJpaEntity entity = createTestEntity(now);

            // When
            Module domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.layerIdValue()).isEqualTo(DEFAULT_LAYER_ID);
            assertThat(domain.nameValue()).isEqualTo("Test Module");
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            ModuleJpaEntity entity = null;

            // When
            Module domain = mapper.toDomain(entity);

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
            Module domain = createTestDomain(now);

            // When
            ModuleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getLayerId()).isEqualTo(DEFAULT_LAYER_ID);
            assertThat(entity.getName()).isEqualTo("Test Module");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            Module domain = null;

            // When
            ModuleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private ModuleJpaEntity createTestEntity(Instant now) {
        return ModuleJpaEntity.of(
                1L,
                DEFAULT_LAYER_ID,
                null,
                "Test Module",
                "Test Description",
                "test/module",
                ":test-module",
                now,
                now,
                null);
    }

    private Module createTestDomain(Instant now) {
        return Module.reconstitute(
                ModuleId.of(1L),
                LayerId.of(DEFAULT_LAYER_ID),
                null,
                ModuleName.of("Test Module"),
                ModuleDescription.of("Test Description"),
                ModulePath.of("test/module"),
                BuildIdentifier.of(":test-module"),
                DeletionStatus.active(),
                now,
                now);
    }
}
