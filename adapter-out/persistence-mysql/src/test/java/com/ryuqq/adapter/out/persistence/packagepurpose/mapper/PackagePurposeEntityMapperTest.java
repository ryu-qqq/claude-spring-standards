package com.ryuqq.adapter.out.persistence.packagepurpose.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackagePurposeEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("PackagePurposeEntityMapper 단위 테스트")
class PackagePurposeEntityMapperTest extends MapperTestSupport {

    private PackagePurposeEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackagePurposeEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            PackagePurposeJpaEntity entity = createTestEntity(now);

            // When
            PackagePurpose domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.structureId()).isEqualTo(PackageStructureId.of(1L));
            assertThat(domain.codeValue()).isEqualTo("DOMAIN");
            assertThat(domain.nameValue()).isEqualTo("Domain Layer");
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            PackagePurposeJpaEntity entity = null;

            // When
            PackagePurpose domain = mapper.toDomain(entity);

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
            PackagePurpose domain = createTestDomain(now);

            // When
            PackagePurposeJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getStructureId()).isEqualTo(1L);
            assertThat(entity.getCode()).isEqualTo("DOMAIN");
            assertThat(entity.getName()).isEqualTo("Domain Layer");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            PackagePurpose domain = null;

            // When
            PackagePurposeJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private PackagePurposeJpaEntity createTestEntity(Instant now) {
        return PackagePurposeJpaEntity.of(
                1L, 1L, "DOMAIN", "Domain Layer", "Test Description", now, now, null);
    }

    private PackagePurpose createTestDomain(Instant now) {
        return PackagePurpose.reconstitute(
                PackagePurposeId.of(1L),
                PackageStructureId.of(1L),
                PurposeCode.of("DOMAIN"),
                PurposeName.of("Domain Layer"),
                "Test Description",
                DeletionStatus.active(),
                now,
                now);
    }
}
