package com.ryuqq.adapter.out.persistence.layerdependency.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
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
 * LayerDependencyRuleQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("LayerDependencyRuleQueryDslRepository 통합 테스트")
class LayerDependencyRuleQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private LayerDependencyRuleQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                LayerDependencyRuleJpaEntity.of(
                        null,
                        100L,
                        "DOMAIN",
                        "APPLICATION",
                        "ALLOWED",
                        "Condition 1",
                        now,
                        now,
                        null),
                LayerDependencyRuleJpaEntity.of(
                        null,
                        100L,
                        "APPLICATION",
                        "PERSISTENCE",
                        "ALLOWED",
                        "Condition 2",
                        now,
                        now,
                        null),
                LayerDependencyRuleJpaEntity.of(
                        null,
                        200L,
                        "DOMAIN",
                        "APPLICATION",
                        "FORBIDDEN",
                        "Condition 3",
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 LayerDependencyRule 조회")
        void success() {
            // Given
            List<LayerDependencyRuleJpaEntity> all = queryDslRepository.findByArchitectureId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<LayerDependencyRuleJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getArchitectureId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findByArchitectureId()")
    class FindByArchitectureId {

        @Test
        @DisplayName("성공 - ArchitectureId로 LayerDependencyRule 목록 조회")
        void success() {
            // When
            List<LayerDependencyRuleJpaEntity> result =
                    queryDslRepository.findByArchitectureId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getArchitectureId().equals(100L));
        }
    }
}
