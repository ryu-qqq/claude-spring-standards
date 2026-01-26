package com.ryuqq.adapter.out.persistence.archunittest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ArchUnitTestJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ArchUnitTestJpaRepository 통합 테스트")
class ArchUnitTestJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private ArchUnitTestJpaRepository archUnitTestJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - ArchUnitTest 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchUnitTestJpaEntity entity =
                    ArchUnitTestJpaEntity.of(
                            null,
                            100L,
                            "TEST-001",
                            "Test Name",
                            "Test Description",
                            "TestClass",
                            "testMethod",
                            "test code",
                            "BLOCKER",
                            now,
                            now,
                            null);

            // When
            ArchUnitTestJpaEntity saved = archUnitTestJpaRepository.save(entity);
            flushAndClear();

            // Then
            ArchUnitTestJpaEntity found =
                    archUnitTestJpaRepository.findById(saved.getId()).orElseThrow();

            assertThat(found.getId()).isNotNull();
            assertThat(found.getCode()).isEqualTo("TEST-001");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ArchUnitTest 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchUnitTestJpaEntity entity =
                    ArchUnitTestJpaEntity.of(
                            null,
                            100L,
                            "TEST-001",
                            "Test Name",
                            "Test Description",
                            "TestClass",
                            "testMethod",
                            "test code",
                            "BLOCKER",
                            now,
                            now,
                            null);

            ArchUnitTestJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ArchUnitTestJpaEntity> result = archUnitTestJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }
}
