package com.ryuqq.adapter.out.persistence.techstack.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
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
 * TechStackQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("TechStackQueryDslRepository 통합 테스트")
class TechStackQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private TechStackQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                TechStackJpaEntity.of(
                        null,
                        "Spring Boot",
                        "ACTIVE",
                        "JAVA",
                        "21",
                        "[]",
                        "SPRING_BOOT",
                        "3.5.0",
                        "[]",
                        "BACKEND",
                        "JVM",
                        "GRADLE",
                        "build.gradle",
                        "[]",
                        now,
                        now,
                        null),
                TechStackJpaEntity.of(
                        null,
                        "Node.js",
                        "ACTIVE",
                        "JAVASCRIPT",
                        "20",
                        "[]",
                        "EXPRESS",
                        "4.18.0",
                        "[]",
                        "BACKEND",
                        "NODE",
                        "NPM",
                        "package.json",
                        "[]",
                        now,
                        now,
                        null),
                TechStackJpaEntity.of(
                        null,
                        "React",
                        "ACTIVE",
                        "JAVASCRIPT",
                        "18",
                        "[]",
                        "REACT",
                        "18.2.0",
                        "[]",
                        "FRONTEND",
                        "BROWSER",
                        "NPM",
                        "package.json",
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
        @DisplayName("성공 - ID로 TechStack 조회")
        void success() {
            // Given - 실제 저장된 엔티티에서 ID 조회
            List<TechStackJpaEntity> all =
                    query(
                            "SELECT t FROM TechStackJpaEntity t ORDER BY t.name",
                            TechStackJpaEntity.class);
            Long id = all.get(1).getId(); // name 정렬: Node.js(0), React(1), Spring Boot(2)

            // When
            Optional<TechStackJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getName()).isEqualTo("React");
        }
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID면 true 반환")
        void success() {
            // Given - 실제 저장된 엔티티에서 ID 조회
            List<TechStackJpaEntity> all =
                    query("SELECT t FROM TechStackJpaEntity t", TechStackJpaEntity.class);
            Long id = all.get(0).getId();

            // When
            boolean result = queryDslRepository.existsById(id);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID면 false 반환")
        void notExists() {
            // Given
            Long id = 999L;

            // When
            boolean result = queryDslRepository.existsById(id);

            // Then
            assertThat(result).isFalse();
        }
    }
}
