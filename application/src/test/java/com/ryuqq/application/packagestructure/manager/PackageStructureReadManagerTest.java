package com.ryuqq.application.packagestructure.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.packagestructure.port.out.PackageStructureQueryPort;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.exception.PackageStructureNotFoundException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
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
 * PackageStructureReadManager 단위 테스트
 *
 * <p>PackageStructure 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("PackageStructureReadManager 단위 테스트")
class PackageStructureReadManagerTest {

    @Mock private PackageStructureQueryPort packageStructureQueryPort;

    @Mock private PackageStructure packageStructure;

    @Mock private PackageStructureSliceCriteria criteria;

    private PackageStructureReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new PackageStructureReadManager(packageStructureQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 PackageStructure 조회")
        void getById_WithValidId_ShouldReturnPackageStructure() {
            // given
            PackageStructureId id = PackageStructureId.of(1L);
            given(packageStructureQueryPort.findById(id)).willReturn(Optional.of(packageStructure));

            // when
            PackageStructure result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(packageStructure);
            then(packageStructureQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            PackageStructureId id = PackageStructureId.of(999L);
            given(packageStructureQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(PackageStructureNotFoundException.class);
            then(packageStructureQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 PackageStructure 조회")
        void findById_WithValidId_ShouldReturnPackageStructure() {
            // given
            PackageStructureId id = PackageStructureId.of(1L);
            given(packageStructureQueryPort.findById(id)).willReturn(Optional.of(packageStructure));

            // when
            PackageStructure result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(packageStructure);
            then(packageStructureQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            PackageStructureId id = PackageStructureId.of(999L);
            given(packageStructureQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            PackageStructure result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(packageStructureQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<PackageStructure> structures = List.of(packageStructure);
            given(packageStructureQueryPort.findBySliceCriteria(criteria)).willReturn(structures);

            // when
            List<PackageStructure> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(packageStructure);
            then(packageStructureQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByModuleIdAndPathPattern 메서드")
    class ExistsByModuleIdAndPathPattern {

        @Test
        @DisplayName("성공 - 모듈 내 경로 패턴 존재 확인")
        void existsByModuleIdAndPathPattern_WhenExists_ShouldReturnTrue() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            PathPattern pathPattern = PathPattern.of("com.ryuqq.domain.aggregate");
            given(packageStructureQueryPort.existsByModuleIdAndPathPattern(moduleId, pathPattern))
                    .willReturn(true);

            // when
            boolean result = sut.existsByModuleIdAndPathPattern(moduleId, pathPattern);

            // then
            assertThat(result).isTrue();
            then(packageStructureQueryPort)
                    .should()
                    .existsByModuleIdAndPathPattern(moduleId, pathPattern);
        }
    }

    @Nested
    @DisplayName("existsByModuleIdAndPathPatternExcluding 메서드")
    class ExistsByModuleIdAndPathPatternExcluding {

        @Test
        @DisplayName("성공 - 특정 구조 제외하고 경로 패턴 존재 확인")
        void existsByModuleIdAndPathPatternExcluding_WhenExists_ShouldReturnTrue() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            PathPattern pathPattern = PathPattern.of("com.ryuqq.domain.aggregate");
            PackageStructureId excludeId = PackageStructureId.of(1L);
            given(
                            packageStructureQueryPort.existsByModuleIdAndPathPatternExcluding(
                                    moduleId, pathPattern, excludeId))
                    .willReturn(true);

            // when
            boolean result =
                    sut.existsByModuleIdAndPathPatternExcluding(moduleId, pathPattern, excludeId);

            // then
            assertThat(result).isTrue();
            then(packageStructureQueryPort)
                    .should()
                    .existsByModuleIdAndPathPatternExcluding(moduleId, pathPattern, excludeId);
        }
    }

    @Nested
    @DisplayName("findByModuleId 메서드")
    class FindByModuleId {

        @Test
        @DisplayName("성공 - 모듈 ID로 패키지 구조 목록 조회")
        void findByModuleId_WithModuleId_ShouldReturnList() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            List<PackageStructure> structures = List.of(packageStructure);
            given(packageStructureQueryPort.findByModuleId(moduleId)).willReturn(structures);

            // when
            List<PackageStructure> result = sut.findByModuleId(moduleId);

            // then
            assertThat(result).hasSize(1).containsExactly(packageStructure);
            then(packageStructureQueryPort).should().findByModuleId(moduleId);
        }
    }
}
