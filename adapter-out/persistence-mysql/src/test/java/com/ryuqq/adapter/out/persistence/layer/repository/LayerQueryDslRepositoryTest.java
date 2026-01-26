package com.ryuqq.adapter.out.persistence.layer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
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
 * LayerQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("LayerQueryDslRepository 통합 테스트")
class LayerQueryDslRepositoryTest extends RepositoryTestSupport {

    private static final Long ARCHITECTURE_ID_1 = 1L;
    private static final Long ARCHITECTURE_ID_2 = 2L;

    @Autowired private LayerQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                LayerJpaEntity.of(
                        null,
                        ARCHITECTURE_ID_1,
                        "DOMAIN",
                        "Domain Layer",
                        "Domain Layer Description",
                        1,
                        now,
                        now,
                        null),
                LayerJpaEntity.of(
                        null,
                        ARCHITECTURE_ID_1,
                        "APPLICATION",
                        "Application Layer",
                        "Application Layer Description",
                        2,
                        now,
                        now,
                        null),
                LayerJpaEntity.of(
                        null,
                        ARCHITECTURE_ID_1,
                        "PERSISTENCE",
                        "Persistence Layer",
                        "Persistence Layer Description",
                        3,
                        now,
                        now,
                        null),
                LayerJpaEntity.of(
                        null,
                        ARCHITECTURE_ID_2,
                        "DOMAIN",
                        "Domain Layer (Arch2)",
                        "Domain Layer for Architecture 2",
                        1,
                        now,
                        now,
                        null),
                // Soft Deleted Layer
                LayerJpaEntity.of(
                        null,
                        ARCHITECTURE_ID_1,
                        "DELETED",
                        "Deleted Layer",
                        "This layer is soft deleted",
                        99,
                        now,
                        now,
                        now));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Layer 조회")
        void success() {
            // Given
            List<LayerJpaEntity> all =
                    queryDslRepository.findBySliceCriteria(LayerSliceCriteria.first(10));
            Long id = all.get(0).getId();

            // When
            Optional<LayerJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("실패 - Soft Delete된 Layer는 조회되지 않음")
        void softDeletedNotFound() {
            // Given - Soft Delete된 Layer 조회 시도
            // setUp에서 deletedAt이 설정된 Layer가 있음

            // When
            List<LayerJpaEntity> all =
                    queryDslRepository.findBySliceCriteria(LayerSliceCriteria.first(100));

            // Then - Soft Delete된 Layer는 포함되지 않음
            assertThat(all).hasSize(4);
            assertThat(all).noneMatch(layer -> "DELETED".equals(layer.getCode()));
        }
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 Layer 확인")
        void exists() {
            // Given
            List<LayerJpaEntity> all =
                    queryDslRepository.findBySliceCriteria(LayerSliceCriteria.first(10));
            Long id = all.get(0).getId();

            // When
            boolean result = queryDslRepository.existsById(id);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 Layer 확인")
        void notExists() {
            // When
            boolean result = queryDslRepository.existsById(999999L);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria()")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 (전체)")
        void firstPage() {
            // Given
            LayerSliceCriteria criteria = LayerSliceCriteria.first(10);

            // When
            List<LayerJpaEntity> result = queryDslRepository.findBySliceCriteria(criteria);

            // Then - Soft Delete 제외 4건
            assertThat(result).hasSize(4);
        }

        @Test
        @DisplayName("성공 - 아키텍처별 조회")
        void byArchitecture() {
            // Given
            LayerSliceCriteria criteria =
                    LayerSliceCriteria.firstByArchitecture(
                            ArchitectureId.of(ARCHITECTURE_ID_1), 10);

            // When
            List<LayerJpaEntity> result = queryDslRepository.findBySliceCriteria(criteria);

            // Then - ARCHITECTURE_ID_1에 속한 Layer 3건 (Soft Delete 제외)
            assertThat(result).hasSize(3);
            assertThat(result)
                    .allMatch(layer -> layer.getArchitectureId().equals(ARCHITECTURE_ID_1));
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이징")
        void cursorPaging() {
            // Given - 첫 페이지에서 2건만 조회
            LayerSliceCriteria firstPage = LayerSliceCriteria.first(2);
            List<LayerJpaEntity> firstResult = queryDslRepository.findBySliceCriteria(firstPage);
            Long lastId = firstResult.get(firstResult.size() - 1).getId();

            // When - 커서 이후 조회
            LayerSliceCriteria afterCursor = LayerSliceCriteria.afterId(lastId, 10);
            List<LayerJpaEntity> result = queryDslRepository.findBySliceCriteria(afterCursor);

            // Then - 나머지 2건
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(layer -> layer.getId() > lastId);
        }

        @Test
        @DisplayName("성공 - orderIndex 오름차순 정렬 확인")
        void orderedByOrderIndex() {
            // Given
            LayerSliceCriteria criteria =
                    LayerSliceCriteria.firstByArchitecture(
                            ArchitectureId.of(ARCHITECTURE_ID_1), 10);

            // When
            List<LayerJpaEntity> result = queryDslRepository.findBySliceCriteria(criteria);

            // Then - orderIndex 오름차순 정렬
            assertThat(result)
                    .isSortedAccordingTo(
                            (a, b) -> {
                                int orderCompare =
                                        Integer.compare(a.getOrderIndex(), b.getOrderIndex());
                                return orderCompare != 0
                                        ? orderCompare
                                        : Long.compare(a.getId(), b.getId());
                            });
        }
    }

    @Nested
    @DisplayName("existsByArchitectureIdAndCode()")
    class ExistsByArchitectureIdAndCode {

        @Test
        @DisplayName("성공 - 코드 중복 확인 (존재함)")
        void exists() {
            // When
            boolean result =
                    queryDslRepository.existsByArchitectureIdAndCode(ARCHITECTURE_ID_1, "DOMAIN");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공 - 코드 중복 확인 (존재하지 않음)")
        void notExists() {
            // When
            boolean result =
                    queryDslRepository.existsByArchitectureIdAndCode(
                            ARCHITECTURE_ID_1, "NON_EXISTENT");

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("성공 - 다른 아키텍처에 같은 코드 존재 가능")
        void sameCodeDifferentArchitecture() {
            // When
            boolean result =
                    queryDslRepository.existsByArchitectureIdAndCode(ARCHITECTURE_ID_2, "DOMAIN");

            // Then - ARCHITECTURE_ID_2에도 DOMAIN 코드가 존재함
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("existsByArchitectureIdAndCodeAndIdNot()")
    class ExistsByArchitectureIdAndCodeAndIdNot {

        @Test
        @DisplayName("성공 - 수정 시 자신 제외 중복 체크")
        void excludeSelf() {
            // Given
            List<LayerJpaEntity> all =
                    queryDslRepository.findBySliceCriteria(LayerSliceCriteria.first(10));
            LayerJpaEntity domainLayer =
                    all.stream()
                            .filter(l -> "DOMAIN".equals(l.getCode()))
                            .filter(l -> l.getArchitectureId().equals(ARCHITECTURE_ID_1))
                            .findFirst()
                            .orElseThrow();

            // When - 자신의 코드로 중복 체크 (자신 제외)
            boolean result =
                    queryDslRepository.existsByArchitectureIdAndCodeAndIdNot(
                            ARCHITECTURE_ID_1, "DOMAIN", domainLayer.getId());

            // Then - 자신을 제외하면 중복 없음
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("성공 - 수정 시 다른 Layer와 중복")
        void duplicateWithOther() {
            // Given
            List<LayerJpaEntity> all =
                    queryDslRepository.findBySliceCriteria(LayerSliceCriteria.first(10));
            LayerJpaEntity applicationLayer =
                    all.stream()
                            .filter(l -> "APPLICATION".equals(l.getCode()))
                            .findFirst()
                            .orElseThrow();

            // When - APPLICATION Layer를 DOMAIN 코드로 변경하려 할 때 중복 체크
            boolean result =
                    queryDslRepository.existsByArchitectureIdAndCodeAndIdNot(
                            ARCHITECTURE_ID_1, "DOMAIN", applicationLayer.getId());

            // Then - DOMAIN 코드가 이미 존재하므로 중복
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("existsByArchitectureId()")
    class ExistsByArchitectureId {

        @Test
        @DisplayName("성공 - Architecture에 Layer 존재함")
        void exists() {
            // When
            boolean result = queryDslRepository.existsByArchitectureId(ARCHITECTURE_ID_1);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공 - Architecture에 Layer 존재하지 않음")
        void notExists() {
            // When
            boolean result = queryDslRepository.existsByArchitectureId(999999L);

            // Then
            assertThat(result).isFalse();
        }
    }
}
