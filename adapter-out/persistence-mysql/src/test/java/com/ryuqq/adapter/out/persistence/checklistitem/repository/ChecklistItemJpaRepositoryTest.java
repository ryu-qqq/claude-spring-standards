package com.ryuqq.adapter.out.persistence.checklistitem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ChecklistItemJpaRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ChecklistItemJpaRepository 통합 테스트")
class ChecklistItemJpaRepositoryTest extends RepositoryTestSupport {

    @Autowired private ChecklistItemJpaRepository checklistItemJpaRepository;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("성공 - ChecklistItem 저장")
        void success() {
            // Given
            Instant now = Instant.now();
            ChecklistItemJpaEntity entity = createTestEntity(now);

            // When
            ChecklistItemJpaEntity saved = checklistItemJpaRepository.save(entity);
            flushAndClear();

            // Then
            ChecklistItemJpaEntity found =
                    checklistItemJpaRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getId()).isNotNull();
            assertThat(found.getRuleId()).isEqualTo(100L);
            assertThat(found.getSequenceOrder()).isEqualTo(1);
            assertThat(found.getCheckDescription()).isEqualTo("Test Check Description");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ChecklistItem 조회")
        void success() {
            // Given
            Instant now = Instant.now();
            ChecklistItemJpaEntity entity = createTestEntity(now);
            ChecklistItemJpaEntity saved = persistAndFlush(entity);
            Long id = saved.getId();

            // When
            Optional<ChecklistItemJpaEntity> result = checklistItemJpaRepository.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }
    }

    // Helper method
    private ChecklistItemJpaEntity createTestEntity(Instant now) {
        return ChecklistItemJpaEntity.of(
                null,
                100L,
                1,
                "Test Check Description",
                "AUTOMATED",
                "SONARQUBE",
                "RULE-001",
                false,
                "MANUAL",
                null,
                now,
                now,
                null);
    }
}
