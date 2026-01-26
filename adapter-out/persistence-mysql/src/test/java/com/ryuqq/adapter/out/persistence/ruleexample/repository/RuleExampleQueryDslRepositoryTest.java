package com.ryuqq.adapter.out.persistence.ruleexample.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
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
 * RuleExampleQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("RuleExampleQueryDslRepository 통합 테스트")
class RuleExampleQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private RuleExampleQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                RuleExampleJpaEntity.of(
                        null,
                        100L,
                        "GOOD",
                        "public class Good { }",
                        "JAVA",
                        "Good example",
                        "[1,2]",
                        "MANUAL",
                        null,
                        now,
                        now,
                        null),
                RuleExampleJpaEntity.of(
                        null,
                        100L,
                        "BAD",
                        "public class Bad { }",
                        "JAVA",
                        "Bad example",
                        "[1,2]",
                        "MANUAL",
                        null,
                        now,
                        now,
                        null),
                RuleExampleJpaEntity.of(
                        null,
                        200L,
                        "GOOD",
                        "public class Good2 { }",
                        "JAVA",
                        "Good example 2",
                        "[1,2]",
                        "MANUAL",
                        null,
                        now,
                        now,
                        null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 RuleExample 조회")
        void success() {
            // Given
            List<RuleExampleJpaEntity> all = queryDslRepository.findByRuleId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<RuleExampleJpaEntity> result = queryDslRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getRuleId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findByRuleId()")
    class FindByRuleId {

        @Test
        @DisplayName("성공 - RuleId로 RuleExample 목록 조회")
        void success() {
            // When
            List<RuleExampleJpaEntity> result = queryDslRepository.findByRuleId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getRuleId().equals(100L));
        }
    }
}
