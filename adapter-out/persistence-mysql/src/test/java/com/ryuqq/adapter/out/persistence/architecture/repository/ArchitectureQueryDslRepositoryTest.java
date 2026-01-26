package com.ryuqq.adapter.out.persistence.architecture.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ArchitectureQueryDslRepository 통합 테스트
 *
 * <p><strong>테스트 범위:</strong>
 *
 * <ul>
 *   <li>복잡한 쿼리 검증
 *   <li>동적 조건 검증
 *   <li>페이징/정렬 동작 검증
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ArchitectureQueryDslRepository 통합 테스트")
class ArchitectureQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ArchitectureQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ArchitectureJpaEntity.of(
                        null,
                        100L,
                        "Hexagonal Architecture",
                        "HEXAGONAL",
                        "Description 1",
                        "[]",
                        "[]",
                        now,
                        now,
                        null),
                ArchitectureJpaEntity.of(
                        null,
                        100L,
                        "Layered Architecture",
                        "LAYERED",
                        "Description 2",
                        "[]",
                        "[]",
                        now,
                        now,
                        null),
                ArchitectureJpaEntity.of(
                        null,
                        200L,
                        "Microservices",
                        "MICROSERVICES",
                        "Description 3",
                        "[]",
                        "[]",
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Architecture 조회")
        void success() {
            // Given
            ArchitectureJpaEntity entity =
                    persistAndFlush(
                            ArchitectureJpaEntity.of(
                                    null,
                                    100L,
                                    "Test Architecture",
                                    "HEXAGONAL",
                                    "Description",
                                    "[]",
                                    "[]",
                                    Instant.now(),
                                    Instant.now(),
                                    null));
            Long id = entity.getId();
            flushAndClear();

            // When
            Optional<ArchitectureJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Architecture")
        void notFound() {
            // Given
            Long nonExistentId = 999999L;

            // When
            Optional<ArchitectureJpaEntity> result = queryDslRepository.findById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 Architecture")
        void exists() {
            // Given
            ArchitectureJpaEntity entity =
                    persistAndFlush(
                            ArchitectureJpaEntity.of(
                                    null,
                                    100L,
                                    "Test Architecture",
                                    "HEXAGONAL",
                                    "Description",
                                    "[]",
                                    "[]",
                                    Instant.now(),
                                    Instant.now(),
                                    null));
            Long id = entity.getId();
            flushAndClear();

            // When
            boolean result = queryDslRepository.existsById(id);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Architecture")
        void notExists() {
            // Given
            Long nonExistentId = 999999L;

            // When
            boolean result = queryDslRepository.existsById(nonExistentId);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByName()")
    class ExistsByName {

        @Test
        @DisplayName("성공 - 존재하는 이름")
        void exists() {
            // Given
            String name = "Hexagonal Architecture";

            // When
            boolean result = queryDslRepository.existsByName(name);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 이름")
        void notExists() {
            // Given
            String name = "Non-existent Architecture";

            // When
            boolean result = queryDslRepository.existsByName(name);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTechStackId()")
    class ExistsByTechStackId {

        @Test
        @DisplayName("성공 - 존재하는 TechStackId")
        void exists() {
            // Given
            Long techStackId = 100L;

            // When
            boolean result = queryDslRepository.existsByTechStackId(techStackId);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 TechStackId")
        void notExists() {
            // Given
            Long techStackId = 999L;

            // When
            boolean result = queryDslRepository.existsByTechStackId(techStackId);

            // Then
            assertThat(result).isFalse();
        }
    }
}
