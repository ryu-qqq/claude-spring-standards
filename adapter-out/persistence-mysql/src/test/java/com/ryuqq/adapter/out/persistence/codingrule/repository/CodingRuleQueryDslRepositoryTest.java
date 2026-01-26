package com.ryuqq.adapter.out.persistence.codingrule.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.zerotolerance.entity.ZeroToleranceRuleJpaEntity;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
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
 * CodingRuleQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("CodingRuleQueryDslRepository 통합 테스트")
class CodingRuleQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private CodingRuleQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();

        // CodingRule 엔티티 생성
        CodingRuleJpaEntity rule1 =
                CodingRuleJpaEntity.ofInstant(
                        null,
                        100L,
                        "DOM-001",
                        "Rule 1",
                        RuleSeverity.BLOCKER,
                        RuleCategory.ANNOTATION,
                        "Desc 1",
                        "Rationale 1",
                        false,
                        "AGGREGATE",
                        now,
                        now,
                        null);
        CodingRuleJpaEntity rule2 =
                CodingRuleJpaEntity.ofInstant(
                        null,
                        100L,
                        "DOM-002",
                        "Rule 2",
                        RuleSeverity.CRITICAL,
                        RuleCategory.ANNOTATION,
                        "Desc 2",
                        "Rationale 2",
                        false,
                        "VALUE_OBJECT",
                        now,
                        now,
                        null);
        CodingRuleJpaEntity rule3 =
                CodingRuleJpaEntity.ofInstant(
                        null,
                        200L,
                        "APP-001",
                        "Rule 3",
                        RuleSeverity.MAJOR,
                        RuleCategory.BEHAVIOR,
                        "Desc 3",
                        "Rationale 3",
                        false,
                        null,
                        now,
                        now,
                        null);

        persistAll(rule1, rule2, rule3);
        flushAndClear();

        // rule1의 ID를 조회하여 ZeroToleranceRule 생성
        List<CodingRuleJpaEntity> rules = queryDslRepository.findByConventionId(100L);
        Long blockerRuleId =
                rules.stream()
                        .filter(r -> r.getCode().equals("DOM-001"))
                        .findFirst()
                        .orElseThrow()
                        .getId();

        // ZeroToleranceRule 생성 (rule1을 참조)
        ZeroToleranceRuleJpaEntity zeroToleranceRule =
                ZeroToleranceRuleJpaEntity.of(
                        null,
                        blockerRuleId,
                        "ANNOTATION",
                        "@Lombok",
                        "REGEX",
                        true,
                        "Lombok 사용 금지",
                        now,
                        now,
                        null);
        persistAll(zeroToleranceRule);
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 CodingRule 조회")
        void success() {
            // Given
            List<CodingRuleJpaEntity> all = queryDslRepository.findByConventionId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<CodingRuleJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    @Nested
    @DisplayName("findByConventionId()")
    class FindByConventionId {

        @Test
        @DisplayName("성공 - ConventionId로 CodingRule 목록 조회")
        void success() {
            // When
            List<CodingRuleJpaEntity> result = queryDslRepository.findByConventionId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getConventionId().equals(100L));
        }
    }

    @Nested
    @DisplayName("findZeroToleranceRules()")
    class FindZeroToleranceRules {

        @Test
        @DisplayName("성공 - Zero-Tolerance 규칙만 조회")
        void success() {
            // When
            List<CodingRuleJpaEntity> result = queryDslRepository.findZeroToleranceRules();

            // Then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("search()")
    class Search {

        @Test
        @DisplayName("성공 - 카테고리로 필터링")
        void filterByCategory() {
            // When
            List<CodingRuleJpaEntity> result =
                    queryDslRepository.search(null, RuleCategory.ANNOTATION, null);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getCategory() == RuleCategory.ANNOTATION);
        }
    }
}
