package com.ryuqq.adapter.out.persistence.convention.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConventionJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ConventionJpaEntityMapper 단위 테스트")
class ConventionJpaEntityMapperTest extends MapperTestSupport {

    private static final Long DEFAULT_MODULE_ID = 1L;

    private ConventionJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ConventionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ConventionJpaEntity entity = createTestEntity(now);

            // When
            Convention domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.moduleIdValue()).isEqualTo(DEFAULT_MODULE_ID);
            assertThat(domain.versionValue()).isEqualTo("1.0.0");
            assertThat(domain.isActive()).isTrue();
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
            Convention domain = createTestDomain(now);

            // When
            ConventionJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getModuleId()).isEqualTo(DEFAULT_MODULE_ID);
            assertThat(entity.getVersion()).isEqualTo("1.0.0");
            assertThat(entity.isActive()).isTrue();
        }
    }

    // Helper methods
    private ConventionJpaEntity createTestEntity(Instant now) {
        return ConventionJpaEntity.of(
                1L, DEFAULT_MODULE_ID, "1.0.0", "Test Description", true, now, now, null);
    }

    private Convention createTestDomain(Instant now) {
        return Convention.reconstitute(
                ConventionId.of(1L),
                ModuleId.of(DEFAULT_MODULE_ID),
                ConventionVersion.of("1.0.0"),
                "Test Description",
                true,
                DeletionStatus.active(),
                now,
                now);
    }
}
