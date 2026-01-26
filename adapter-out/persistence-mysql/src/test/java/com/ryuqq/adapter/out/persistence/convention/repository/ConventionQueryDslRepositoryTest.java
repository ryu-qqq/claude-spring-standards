package com.ryuqq.adapter.out.persistence.convention.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.RepositoryTestSupport;
import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
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
 * ConventionQueryDslRepository 통합 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@Tag("integration")
@Tag("repository")
@Tag("persistence-layer")
@DisplayName("ConventionQueryDslRepository 통합 테스트")
class ConventionQueryDslRepositoryTest extends RepositoryTestSupport {

    @Autowired private ConventionQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Instant now = Instant.now();
        persistAll(
                ConventionJpaEntity.of(
                        null, 1L, "1.0.0", "Domain Module Convention", true, now, now, null),
                ConventionJpaEntity.of(
                        null, 2L, "1.0.0", "Application Module Convention", true, now, now, null),
                ConventionJpaEntity.of(
                        null, 1L, "2.0.0", "Domain Module Convention v2", false, now, now, null));
        flushAndClear();
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Convention 조회")
        void success() {
            // Given
            List<ConventionJpaEntity> all = queryDslRepository.findAllActive();
            Long id = all.get(0).getId();

            // When
            Optional<ConventionJpaEntity> result = queryDslRepository.findById(id);

            // Then - moduleId 오름차순 정렬이므로 moduleId=1이 먼저 옴
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
            assertThat(result.get().getModuleId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("findAllActive()")
    class FindAllActive {

        @Test
        @DisplayName("성공 - 활성화된 Convention 목록 조회")
        void success() {
            // When
            List<ConventionJpaEntity> result = queryDslRepository.findAllActive();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(ConventionJpaEntity::isActive);
        }
    }

    @Nested
    @DisplayName("findActiveByModuleId()")
    class FindActiveByModuleId {

        @Test
        @DisplayName("성공 - 모듈 ID로 활성화된 Convention 조회")
        void success() {
            // When
            Optional<ConventionJpaEntity> result = queryDslRepository.findActiveByModuleId(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getModuleId()).isEqualTo(1L);
            assertThat(result.get().isActive()).isTrue();
        }
    }
}
