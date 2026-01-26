package com.ryuqq.adapter.out.persistence.codingrule.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CodingRuleJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("CodingRuleJpaRepository 통합 테스트")
class CodingRuleJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private CodingRuleJpaRepository codingRuleJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - CodingRule 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            CodingRuleJpaEntity entity =
                    CodingRuleJpaEntity.ofInstant(
                            null,
                            100L,
                            "DOM-001",
                            "Test Rule",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            "AGGREGATE",
                            now,
                            now,
                            null);

            // When
            CodingRuleJpaEntity saved = codingRuleJpaRepository.save(entity);
            flushAndClear();

            // Then
            CodingRuleJpaEntity found =
                    codingRuleJpaRepository.findById(saved.getId()).orElseThrow();

            assertThat(found.getId()).isNotNull();
            assertThat(found.getCode()).isEqualTo("DOM-001");
            assertThat(found.getName()).isEqualTo("Test Rule");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 CodingRule 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            CodingRuleJpaEntity entity =
                    CodingRuleJpaEntity.ofInstant(
                            null,
                            100L,
                            "DOM-001",
                            "Test Rule",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            null,
                            now,
                            now,
                            null);

            CodingRuleJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<CodingRuleJpaEntity> result = codingRuleJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getCode()).isEqualTo("DOM-001");
        }
    }
}
