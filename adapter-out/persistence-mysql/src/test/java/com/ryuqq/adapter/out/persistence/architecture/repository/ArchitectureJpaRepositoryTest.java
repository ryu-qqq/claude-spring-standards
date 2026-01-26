package com.ryuqq.adapter.out.persistence.architecture.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ArchitectureJpaRepository 통합 테스트
 *
 * <p><strong>테스트 범위:</strong>
 *
 * <ul>
 *   <li>기본 CRUD 동작 검증
 *   <li>커스텀 쿼리 메서드 검증
 *   <li>데이터 정합성 검증
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ArchitectureJpaRepository 통합 테스트")
class ArchitectureJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private ArchitectureJpaRepository architectureJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - Architecture 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchitectureJpaEntity entity =
                    ArchitectureJpaEntity.of(
                            null,
                            100L,
                            "Test Architecture",
                            "HEXAGONAL",
                            "Test Description",
                            "[]",
                            "[]",
                            now,
                            now,
                            null);

            // When
            ArchitectureJpaEntity saved = architectureJpaRepository.save(entity);
            flushAndClear();

            // Then
            ArchitectureJpaEntity found =
                    architectureJpaRepository.findById(saved.getId()).orElseThrow();

            assertThat(found.getId()).isNotNull();
            assertThat(found.getTechStackId()).isEqualTo(100L);
            assertThat(found.getName()).isEqualTo("Test Architecture");
            assertThat(found.getPatternType()).isEqualTo("HEXAGONAL");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Architecture 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchitectureJpaEntity entity =
                    ArchitectureJpaEntity.of(
                            null,
                            100L,
                            "Test Architecture",
                            "HEXAGONAL",
                            "Test Description",
                            "[]",
                            "[]",
                            now,
                            now,
                            null);

            ArchitectureJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ArchitectureJpaEntity> result = architectureJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getName()).isEqualTo("Test Architecture");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Architecture")
        void notFound() {
            // Given
            Long nonExistentId = 999999L;

            // When
            Optional<ArchitectureJpaEntity> result =
                    architectureJpaRepository.findById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteById {

        @Test
        @DisplayName("성공 - Architecture 삭제")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchitectureJpaEntity entity =
                    ArchitectureJpaEntity.of(
                            null,
                            100L,
                            "Test Architecture",
                            "HEXAGONAL",
                            "Test Description",
                            "[]",
                            "[]",
                            now,
                            now,
                            null);

            ArchitectureJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();
            assertThat(architectureJpaRepository.existsById(id)).isTrue();

            // When
            architectureJpaRepository.deleteById(id);
            flushAndClear();

            // Then
            assertThat(architectureJpaRepository.existsById(id)).isFalse();
        }
    }
}
