package com.ryuqq.adapter.out.persistence.architecture.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.config.PersistenceObjectMapper;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ArchitectureEntityMapper 단위 테스트
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
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ArchitectureEntityMapper 단위 테스트")
class ArchitectureEntityMapperTest extends MapperTestSupport {

    @Mock private PersistenceObjectMapper objectMapper;

    private ArchitectureEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchitectureEntityMapper(objectMapper);
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchitectureJpaEntity entity =
                    ArchitectureJpaEntity.of(
                            1L,
                            100L,
                            "Hexagonal Architecture",
                            "HEXAGONAL",
                            "Ports and Adapters pattern",
                            "[\"Dependency Inversion\", \"Separation of Concerns\"]",
                            "[]",
                            now,
                            now,
                            null);

            when(objectMapper.readValueAsStringList(
                            "[\"Dependency Inversion\", \"Separation of Concerns\"]"))
                    .thenReturn(List.of("Dependency Inversion", "Separation of Concerns"));

            // When
            Architecture domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.techStackIdValue()).isEqualTo(100L);
            assertThat(domain.nameValue()).isEqualTo("Hexagonal Architecture");
            assertThat(domain.patternTypeName()).isEqualTo("HEXAGONAL");
            assertThat(domain.patternDescriptionValue()).isEqualTo("Ports and Adapters pattern");
            assertThat(domain.createdAt()).isEqualTo(now);
            assertThat(domain.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            ArchitectureJpaEntity entity = null;

            // When
            Architecture domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNull();
        }

        @Test
        @DisplayName("성공 - 삭제된 Entity 변환")
        void deletedEntity() {
            // Given
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(100);
            ArchitectureJpaEntity entity =
                    ArchitectureJpaEntity.of(
                            1L,
                            100L,
                            "Test Architecture",
                            "HEXAGONAL",
                            null,
                            "[]",
                            "[]",
                            now,
                            now,
                            deletedAt);

            when(objectMapper.readValueAsStringList("[]")).thenReturn(List.of());

            // When
            Architecture domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("성공 - 빈 patternDescription 처리")
        void emptyPatternDescription() {
            // Given
            Instant now = Instant.now();
            ArchitectureJpaEntity entity =
                    ArchitectureJpaEntity.of(
                            1L,
                            100L,
                            "Test Architecture",
                            "HEXAGONAL",
                            null,
                            "[]",
                            "[]",
                            now,
                            now,
                            null);

            when(objectMapper.readValueAsStringList("[]")).thenReturn(List.of());

            // When
            Architecture domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.patternDescription()).isNotNull();
            assertThat(domain.patternDescription().isEmpty()).isTrue();
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
            Architecture domain =
                    Architecture.reconstitute(
                            ArchitectureId.forNew(),
                            TechStackId.of(100L),
                            ArchitectureName.of("New Architecture"),
                            PatternType.HEXAGONAL,
                            PatternDescription.of("Description"),
                            PatternPrinciples.of(List.of("Principle1")),
                            ReferenceLinks.empty(),
                            DeletionStatus.active(),
                            now,
                            now);

            when(objectMapper.writeValueAsString(List.of("Principle1")))
                    .thenReturn("[\"Principle1\"]");

            // When
            ArchitectureJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isNull(); // 신규이므로 ID 없음
            assertThat(entity.getTechStackId()).isEqualTo(100L);
            assertThat(entity.getName()).isEqualTo("New Architecture");
            assertThat(entity.getPatternType()).isEqualTo("HEXAGONAL");
            assertThat(entity.getPatternDescription()).isEqualTo("Description");
        }

        @Test
        @DisplayName("성공 - 기존 Domain을 Entity로 변환 (ID 유지)")
        void existingDomain() {
            // Given
            Instant now = Instant.now();
            Architecture domain =
                    Architecture.reconstitute(
                            ArchitectureId.of(1L),
                            TechStackId.of(100L),
                            ArchitectureName.of("Existing Architecture"),
                            PatternType.HEXAGONAL,
                            PatternDescription.of("Description"),
                            PatternPrinciples.of(List.of("Principle1")),
                            ReferenceLinks.empty(),
                            DeletionStatus.active(),
                            now,
                            now);

            when(objectMapper.writeValueAsString(List.of("Principle1")))
                    .thenReturn("[\"Principle1\"]");

            // When
            ArchitectureJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getName()).isEqualTo("Existing Architecture");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            Architecture domain = null;

            // When
            ArchitectureJpaEntity entity = mapper.toEntity(domain);

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
            Architecture original =
                    Architecture.reconstitute(
                            ArchitectureId.of(1L),
                            TechStackId.of(100L),
                            ArchitectureName.of("Test Architecture"),
                            PatternType.HEXAGONAL,
                            PatternDescription.of("Description"),
                            PatternPrinciples.of(List.of("Principle1", "Principle2")),
                            ReferenceLinks.empty(),
                            DeletionStatus.active(),
                            now,
                            now);

            when(objectMapper.writeValueAsString(List.of("Principle1", "Principle2")))
                    .thenReturn("[\"Principle1\", \"Principle2\"]");
            when(objectMapper.readValueAsStringList("[\"Principle1\", \"Principle2\"]"))
                    .thenReturn(List.of("Principle1", "Principle2"));

            // When
            ArchitectureJpaEntity entity = mapper.toEntity(original);
            Architecture converted = mapper.toDomain(entity);

            // Then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.techStackIdValue()).isEqualTo(original.techStackIdValue());
            assertThat(converted.nameValue()).isEqualTo(original.nameValue());
            assertThat(converted.patternTypeName()).isEqualTo(original.patternTypeName());
            assertThat(converted.patternDescriptionValue())
                    .isEqualTo(original.patternDescriptionValue());
        }

        @Test
        @DisplayName("성공 - Entity → Domain → Entity 변환 후 동일성 유지")
        void entityToDomainToEntity() {
            // Given
            Instant now = Instant.now();
            ArchitectureJpaEntity original =
                    ArchitectureJpaEntity.of(
                            1L,
                            100L,
                            "Test Architecture",
                            "HEXAGONAL",
                            "Description",
                            "[\"Principle1\"]",
                            "[]",
                            now,
                            now,
                            null);

            when(objectMapper.readValueAsStringList("[\"Principle1\"]"))
                    .thenReturn(List.of("Principle1"));
            when(objectMapper.writeValueAsString(List.of("Principle1")))
                    .thenReturn("[\"Principle1\"]");

            // When
            Architecture domain = mapper.toDomain(original);
            ArchitectureJpaEntity converted = mapper.toEntity(domain);

            // Then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getTechStackId()).isEqualTo(original.getTechStackId());
            assertThat(converted.getName()).isEqualTo(original.getName());
            assertThat(converted.getPatternType()).isEqualTo(original.getPatternType());
            assertThat(converted.getPatternDescription())
                    .isEqualTo(original.getPatternDescription());
        }
    }
}
