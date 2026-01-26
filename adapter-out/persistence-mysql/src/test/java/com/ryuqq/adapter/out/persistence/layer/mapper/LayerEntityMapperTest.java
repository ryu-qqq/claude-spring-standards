package com.ryuqq.adapter.out.persistence.layer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LayerEntityMapper 단위 테스트
 *
 * <p><strong>테스트 범위:</strong>
 *
 * <ul>
 *   <li>toDomain() - Entity → Domain 변환
 *   <li>toEntity() - Domain → Entity 변환
 *   <li>양방향 변환 정합성
 *   <li>엣지 케이스 처리
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("LayerEntityMapper 단위 테스트")
class LayerEntityMapperTest extends MapperTestSupport {

    private LayerEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LayerEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            LayerJpaEntity entity =
                    LayerJpaEntity.of(
                            1L, 100L, "DOMAIN", "Domain Layer", "도메인 계층입니다.", 1, now, now, null);

            // When
            Layer domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.architectureIdValue()).isEqualTo(100L);
            assertThat(domain.codeValue()).isEqualTo("DOMAIN");
            assertThat(domain.nameValue()).isEqualTo("Domain Layer");
            assertThat(domain.description()).isEqualTo("도메인 계층입니다.");
            assertThat(domain.orderIndex()).isEqualTo(1);
            assertThat(domain.createdAt()).isEqualTo(now);
            assertThat(domain.updatedAt()).isEqualTo(now);
            assertThat(domain.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            LayerJpaEntity entity = null;

            // When
            Layer domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNull();
        }

        @Test
        @DisplayName("성공 - 삭제된 Entity 변환")
        void deletedEntity() {
            // Given
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(100);
            LayerJpaEntity entity =
                    LayerJpaEntity.of(
                            1L, 100L, "DOMAIN", "Domain Layer", null, 1, now, now, deletedAt);

            // When
            Layer domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("성공 - 빈 description 처리")
        void emptyDescription() {
            // Given
            Instant now = Instant.now();
            LayerJpaEntity entity =
                    LayerJpaEntity.of(1L, 100L, "DOMAIN", "Domain Layer", null, 1, now, now, null);

            // When
            Layer domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.description()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("성공 - 신규 Domain을 Entity로 변환")
        void newDomain() {
            // Given
            Instant now = Instant.now();
            Layer domain =
                    Layer.reconstitute(
                            LayerId.forNew(),
                            ArchitectureId.of(100L),
                            LayerCode.of("APPLICATION"),
                            LayerName.of("Application Layer"),
                            "애플리케이션 계층입니다.",
                            2,
                            DeletionStatus.active(),
                            now,
                            now);

            // When
            LayerJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isNull(); // 신규이므로 ID 없음
            assertThat(entity.getArchitectureId()).isEqualTo(100L);
            assertThat(entity.getCode()).isEqualTo("APPLICATION");
            assertThat(entity.getName()).isEqualTo("Application Layer");
            assertThat(entity.getDescription()).isEqualTo("애플리케이션 계층입니다.");
            assertThat(entity.getOrderIndex()).isEqualTo(2);
        }

        @Test
        @DisplayName("성공 - 기존 Domain을 Entity로 변환 (ID 유지)")
        void existingDomain() {
            // Given
            Instant now = Instant.now();
            Layer domain =
                    Layer.reconstitute(
                            LayerId.of(1L),
                            ArchitectureId.of(100L),
                            LayerCode.of("PERSISTENCE"),
                            LayerName.of("Persistence Layer"),
                            "영속성 계층입니다.",
                            3,
                            DeletionStatus.active(),
                            now,
                            now);

            // When
            LayerJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getName()).isEqualTo("Persistence Layer");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            Layer domain = null;

            // When
            LayerJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    @Nested
    @DisplayName("양방향 변환 정합성")
    class RoundTripConversion {

        @Test
        @DisplayName("성공 - Domain → Entity → Domain 변환 후 동일성 유지")
        void domainToEntityToDomain() {
            // Given
            Instant now = Instant.now();
            Layer original =
                    Layer.reconstitute(
                            LayerId.of(1L),
                            ArchitectureId.of(100L),
                            LayerCode.of("REST_API"),
                            LayerName.of("REST API Layer"),
                            "REST API 계층입니다.",
                            4,
                            DeletionStatus.active(),
                            now,
                            now);

            // When
            LayerJpaEntity entity = mapper.toEntity(original);
            Layer converted = mapper.toDomain(entity);

            // Then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.architectureIdValue()).isEqualTo(original.architectureIdValue());
            assertThat(converted.codeValue()).isEqualTo(original.codeValue());
            assertThat(converted.nameValue()).isEqualTo(original.nameValue());
            assertThat(converted.description()).isEqualTo(original.description());
            assertThat(converted.orderIndex()).isEqualTo(original.orderIndex());
        }

        @Test
        @DisplayName("성공 - Entity → Domain → Entity 변환 후 동일성 유지")
        void entityToDomainToEntity() {
            // Given
            Instant now = Instant.now();
            LayerJpaEntity original =
                    LayerJpaEntity.of(
                            1L,
                            100L,
                            "INFRASTRUCTURE",
                            "Infrastructure Layer",
                            "인프라스트럭처 계층입니다.",
                            5,
                            now,
                            now,
                            null);

            // When
            Layer domain = mapper.toDomain(original);
            LayerJpaEntity converted = mapper.toEntity(domain);

            // Then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getArchitectureId()).isEqualTo(original.getArchitectureId());
            assertThat(converted.getCode()).isEqualTo(original.getCode());
            assertThat(converted.getName()).isEqualTo(original.getName());
            assertThat(converted.getDescription()).isEqualTo(original.getDescription());
            assertThat(converted.getOrderIndex()).isEqualTo(original.getOrderIndex());
        }
    }
}
