package com.ryuqq.adapter.out.persistence.archunittest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
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
 * ArchUnitTestQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ArchUnitTestQueryDslRepository 통합 테스트")
class ArchUnitTestQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ArchUnitTestQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ArchUnitTestJpaEntity.of(
                        null,
                        100L,
                        "TEST-001",
                        "Test 1",
                        "Desc 1",
                        "Class1",
                        "method1",
                        "code1",
                        "BLOCKER",
                        now,
                        now,
                        null),
                ArchUnitTestJpaEntity.of(
                        null,
                        100L,
                        "TEST-002",
                        "Test 2",
                        "Desc 2",
                        "Class2",
                        "method2",
                        "code2",
                        "CRITICAL",
                        now,
                        now,
                        null),
                ArchUnitTestJpaEntity.of(
                        null,
                        200L,
                        "TEST-003",
                        "Test 3",
                        "Desc 3",
                        "Class3",
                        "method3",
                        "code3",
                        "MAJOR",
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ArchUnitTest 조회")
        void success() {
            // Given
            List<ArchUnitTestJpaEntity> all = queryDslRepository.findByStructureId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<ArchUnitTestJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    @Nested
    @DisplayName("findByStructureId()")
    class FindByStructureId {

        @Test
        @DisplayName("성공 - StructureId로 ArchUnitTest 목록 조회")
        void success() {
            // When
            List<ArchUnitTestJpaEntity> result = queryDslRepository.findByStructureId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStructureId().equals(100L));
        }
    }
}
