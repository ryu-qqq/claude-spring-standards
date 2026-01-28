package com.ryuqq.adapter.out.persistence.packagepurpose.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
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
 * PackagePurposeQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("PackagePurposeQueryDslRepository 통합 테스트")
class PackagePurposeQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private PackagePurposeQueryDslRepository queryDslRepository;

    private static final Long STRUCTURE_ID_1 = 1L;
    private static final Long STRUCTURE_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                PackagePurposeJpaEntity.of(
                        null, STRUCTURE_ID_1, "AGGREGATE", "Aggregate", "Desc 1", now, now, null),
                PackagePurposeJpaEntity.of(
                        null,
                        STRUCTURE_ID_1,
                        "VALUE_OBJECT",
                        "Value Object",
                        "Desc 2",
                        now,
                        now,
                        null),
                PackagePurposeJpaEntity.of(
                        null, STRUCTURE_ID_2, "SERVICE", "Service", "Desc 3", now, now, null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 PackagePurpose 조회")
        void success() {
            // Given
            List<PackagePurposeJpaEntity> all =
                    queryDslRepository.findByStructureId(STRUCTURE_ID_1);
            Long id = all.get(0).getId();

            // When
            Optional<PackagePurposeJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getStructureId()).isEqualTo(STRUCTURE_ID_1);
        }
    }

    @Nested
    @DisplayName("findByStructureId()")
    class FindByStructureId {

        @Test
        @DisplayName("성공 - StructureId로 PackagePurpose 목록 조회")
        void success() {
            // When
            List<PackagePurposeJpaEntity> result =
                    queryDslRepository.findByStructureId(STRUCTURE_ID_1);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStructureId().equals(STRUCTURE_ID_1));
        }
    }
}
