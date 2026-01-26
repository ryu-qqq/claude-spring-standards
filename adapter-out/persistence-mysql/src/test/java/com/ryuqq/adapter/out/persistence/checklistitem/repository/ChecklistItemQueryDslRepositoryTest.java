package com.ryuqq.adapter.out.persistence.checklistitem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
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
 * ChecklistItemQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ChecklistItemQueryDslRepository 통합 테스트")
class ChecklistItemQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ChecklistItemQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ChecklistItemJpaEntity.of(
                        null,
                        100L,
                        1,
                        "Check 1",
                        "AUTOMATED",
                        "SONARQUBE",
                        "RULE-001",
                        false,
                        "MANUAL",
                        null,
                        now,
                        now,
                        null),
                ChecklistItemJpaEntity.of(
                        null, 100L, 2, "Check 2", "MANUAL", null, null, true, "MANUAL", null, now,
                        now, null),
                ChecklistItemJpaEntity.of(
                        null,
                        200L,
                        1,
                        "Check 3",
                        "AUTOMATED",
                        "CHECKSTYLE",
                        "RULE-002",
                        false,
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
        @DisplayName("성공 - ID로 ChecklistItem 조회")
        void success() {
            // Given
            List<ChecklistItemJpaEntity> all = queryDslRepository.findByRuleId(100L);
            Long id = all.get(0).getId();

            // When
            Optional<ChecklistItemJpaEntity> result = queryDslRepository.findById(id);

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
        @DisplayName("성공 - RuleId로 ChecklistItem 목록 조회")
        void success() {
            // When
            List<ChecklistItemJpaEntity> result = queryDslRepository.findByRuleId(100L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getRuleId().equals(100L));
        }
    }
}
