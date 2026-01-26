package com.ryuqq.application.convention.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.convention.port.out.ConventionQueryPort;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConventionReadManager 단위 테스트
 *
 * <p>Convention 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ConventionReadManager 단위 테스트")
class ConventionReadManagerTest {

    @Mock private ConventionQueryPort conventionQueryPort;

    @Mock private Convention convention;

    @Mock private ConventionSliceCriteria criteria;

    private ConventionReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ConventionReadManager(conventionQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Convention 조회")
        void findById_WithValidId_ShouldReturnConvention() {
            // given
            ConventionId id = ConventionId.of(1L);
            given(conventionQueryPort.findById(id)).willReturn(Optional.of(convention));

            // when
            Optional<Convention> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(convention);
            then(conventionQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            ConventionId id = ConventionId.of(999L);
            given(conventionQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Convention> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(conventionQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            ConventionId id = ConventionId.of(1L);
            given(conventionQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(conventionQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findAllActive 메서드")
    class FindAllActive {

        @Test
        @DisplayName("성공 - 활성화된 컨벤션 목록 조회")
        void findAllActive_ShouldReturnActiveConventions() {
            // given
            List<Convention> conventions = List.of(convention);
            given(conventionQueryPort.findAllActive()).willReturn(conventions);

            // when
            List<Convention> result = sut.findAllActive();

            // then
            assertThat(result).hasSize(1).containsExactly(convention);
            then(conventionQueryPort).should().findAllActive();
        }
    }

    @Nested
    @DisplayName("findActiveByModuleId 메서드")
    class FindActiveByModuleId {

        @Test
        @DisplayName("성공 - 모듈별 활성화된 컨벤션 조회")
        void findActiveByModuleId_WithModuleId_ShouldReturnConvention() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            given(conventionQueryPort.findActiveByModuleId(moduleId))
                    .willReturn(Optional.of(convention));

            // when
            Optional<Convention> result = sut.findActiveByModuleId(moduleId);

            // then
            assertThat(result).isPresent().contains(convention);
            then(conventionQueryPort).should().findActiveByModuleId(moduleId);
        }
    }

    @Nested
    @DisplayName("existsByModuleIdAndVersion 메서드")
    class ExistsByModuleIdAndVersion {

        @Test
        @DisplayName("성공 - 모듈+버전 중복 확인")
        void existsByModuleIdAndVersion_WhenExists_ShouldReturnTrue() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            String version = "1.0.0";
            given(conventionQueryPort.existsByModuleIdAndVersion(moduleId, version))
                    .willReturn(true);

            // when
            boolean result = sut.existsByModuleIdAndVersion(moduleId, version);

            // then
            assertThat(result).isTrue();
            then(conventionQueryPort).should().existsByModuleIdAndVersion(moduleId, version);
        }
    }

    @Nested
    @DisplayName("existsByModuleIdAndVersionAndIdNot 메서드")
    class ExistsByModuleIdAndVersionAndIdNot {

        @Test
        @DisplayName("성공 - 특정 ID 제외하고 모듈+버전 중복 확인")
        void existsByModuleIdAndVersionAndIdNot_WhenExists_ShouldReturnTrue() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            String version = "1.0.0";
            ConventionId excludeId = ConventionId.of(1L);
            given(
                            conventionQueryPort.existsByModuleIdAndVersionAndIdNot(
                                    moduleId, version, excludeId))
                    .willReturn(true);

            // when
            boolean result = sut.existsByModuleIdAndVersionAndIdNot(moduleId, version, excludeId);

            // then
            assertThat(result).isTrue();
            then(conventionQueryPort)
                    .should()
                    .existsByModuleIdAndVersionAndIdNot(moduleId, version, excludeId);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<Convention> conventions = List.of(convention);
            given(conventionQueryPort.findBySliceCriteria(criteria)).willReturn(conventions);

            // when
            List<Convention> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(convention);
            then(conventionQueryPort).should().findBySliceCriteria(criteria);
        }
    }
}
