package com.ryuqq.adapter.out.persistence.ruleexample.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RuleExampleJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("RuleExampleJpaRepository 통합 테스트")
class RuleExampleJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private RuleExampleJpaRepository ruleExampleJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - RuleExample 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            RuleExampleJpaEntity entity = createTestEntity(now);

            // When
            RuleExampleJpaEntity saved = ruleExampleJpaRepository.save(entity);
            flushAndClear();

            // Then
            RuleExampleJpaEntity found =
                    ruleExampleJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getRuleId()).isEqualTo(100L);
            assertThat(found.getExampleType()).isEqualTo("GOOD");
            assertThat(found.getCode()).isEqualTo("public class Test { }");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 RuleExample 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            RuleExampleJpaEntity entity = createTestEntity(now);
            RuleExampleJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<RuleExampleJpaEntity> result = ruleExampleJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private RuleExampleJpaEntity createTestEntity(Instant now) {
        return RuleExampleJpaEntity.of(
                null,
                100L,
                "GOOD",
                "public class Test { }",
                "JAVA",
                "Test Explanation",
                "[1,2]",
                "MANUAL",
                null,
                now,
                now,
                null);
    }
}
