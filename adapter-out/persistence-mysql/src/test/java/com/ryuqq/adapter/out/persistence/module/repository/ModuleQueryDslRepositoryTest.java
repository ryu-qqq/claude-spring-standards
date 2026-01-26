package com.ryuqq.adapter.out.persistence.module.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
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
 * ModuleQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ModuleQueryDslRepository 통합 테스트")
class ModuleQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ModuleQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ModuleJpaEntity.of(
                        null,
                        100L,
                        null,
                        "Module 1",
                        "Desc 1",
                        "module1",
                        ":module1",
                        now,
                        now,
                        null),
                ModuleJpaEntity.of(
                        null,
                        100L,
                        null,
                        "Module 2",
                        "Desc 2",
                        "module2",
                        ":module2",
                        now,
                        now,
                        null),
                ModuleJpaEntity.of(
                        null,
                        200L,
                        null,
                        "Module 3",
                        "Desc 3",
                        "module3",
                        ":module3",
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Module 조회")
        void success() {
            // Given
            List<ModuleJpaEntity> all = queryDslRepository.findAllByLayerId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<ModuleJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getLayerId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findAllByLayerId()")
    class FindAllByLayerId {

        @Test
        @DisplayName("성공 - LayerId로 Module 목록 조회")
        void success() {
            // When
            List<ModuleJpaEntity> result = queryDslRepository.findAllByLayerId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getLayerId().equals(100L));
        }
    }
}
