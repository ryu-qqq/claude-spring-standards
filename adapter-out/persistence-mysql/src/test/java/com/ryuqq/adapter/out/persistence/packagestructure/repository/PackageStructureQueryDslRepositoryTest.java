package com.ryuqq.adapter.out.persistence.packagestructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
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
 * PackageStructureQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("PackageStructureQueryDslRepository 통합 테스트")
class PackageStructureQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private PackageStructureQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                PackageStructureJpaEntity.of(
                        null,
                        100L,
                        "com.test1",
                        "[]",
                        "Test1.*",
                        "Test1",
                        "Desc 1",
                        now,
                        now,
                        null),
                PackageStructureJpaEntity.of(
                        null,
                        100L,
                        "com.test2",
                        "[]",
                        "Test2.*",
                        "Test2",
                        "Desc 2",
                        now,
                        now,
                        null),
                PackageStructureJpaEntity.of(
                        null,
                        200L,
                        "com.test3",
                        "[]",
                        "Test3.*",
                        "Test3",
                        "Desc 3",
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 PackageStructure 조회")
        void success() {
            // Given
            List<PackageStructureJpaEntity> all = queryDslRepository.findByModuleId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<PackageStructureJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getModuleId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findByModuleId()")
    class FindByModuleId {

        @Test
        @DisplayName("성공 - ModuleId로 PackageStructure 목록 조회")
        void success() {
            // When
            List<PackageStructureJpaEntity> result = queryDslRepository.findByModuleId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getModuleId().equals(100L));
        }
    }
}
