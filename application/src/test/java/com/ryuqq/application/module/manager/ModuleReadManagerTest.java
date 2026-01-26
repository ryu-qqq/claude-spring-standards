package com.ryuqq.application.module.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.module.port.out.ModuleQueryPort;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import com.ryuqq.domain.module.vo.ModuleName;
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
 * ModuleReadManager 단위 테스트
 *
 * <p>Module 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ModuleReadManager 단위 테스트")
class ModuleReadManagerTest {

    @Mock private ModuleQueryPort moduleQueryPort;

    @Mock private Module module;

    @Mock private ModuleSliceCriteria criteria;

    private ModuleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ModuleReadManager(moduleQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Module 조회")
        void findById_WithValidId_ShouldReturnModule() {
            // given
            ModuleId id = ModuleId.of(1L);
            given(moduleQueryPort.findById(id)).willReturn(Optional.of(module));

            // when
            Optional<Module> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(module);
            then(moduleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            ModuleId id = ModuleId.of(999L);
            given(moduleQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Module> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(moduleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 Module 조회")
        void getById_WithValidId_ShouldReturnModule() {
            // given
            ModuleId id = ModuleId.of(1L);
            given(moduleQueryPort.findById(id)).willReturn(Optional.of(module));

            // when
            Module result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(module);
            then(moduleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 조회 시 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            ModuleId id = ModuleId.of(999L);
            given(moduleQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(ModuleNotFoundException.class);
            then(moduleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<Module> modules = List.of(module);
            given(moduleQueryPort.findBySliceCriteria(criteria)).willReturn(modules);

            // when
            List<Module> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(module);
            then(moduleQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findAllByLayerId 메서드")
    class FindAllByLayerId {

        @Test
        @DisplayName("성공 - LayerId로 전체 모듈 조회")
        void findAllByLayerId_WithLayerId_ShouldReturnList() {
            // given
            LayerId layerId = LayerId.of(1L);
            List<Module> modules = List.of(module);
            given(moduleQueryPort.findAllByLayerId(layerId)).willReturn(modules);

            // when
            List<Module> result = sut.findAllByLayerId(layerId);

            // then
            assertThat(result).hasSize(1).containsExactly(module);
            then(moduleQueryPort).should().findAllByLayerId(layerId);
        }
    }

    @Nested
    @DisplayName("existsByLayerIdAndName 메서드")
    class ExistsByLayerIdAndName {

        @Test
        @DisplayName("성공 - 레이어 내 모듈 이름 존재 확인")
        void existsByLayerIdAndName_WhenExists_ShouldReturnTrue() {
            // given
            LayerId layerId = LayerId.of(1L);
            ModuleName name = ModuleName.of("aggregate");
            given(moduleQueryPort.existsByLayerIdAndName(layerId, name)).willReturn(true);

            // when
            boolean result = sut.existsByLayerIdAndName(layerId, name);

            // then
            assertThat(result).isTrue();
            then(moduleQueryPort).should().existsByLayerIdAndName(layerId, name);
        }
    }

    @Nested
    @DisplayName("existsByLayerIdAndNameExcluding 메서드")
    class ExistsByLayerIdAndNameExcluding {

        @Test
        @DisplayName("성공 - 특정 모듈 제외하고 레이어 내 이름 존재 확인")
        void existsByLayerIdAndNameExcluding_WhenExists_ShouldReturnTrue() {
            // given
            LayerId layerId = LayerId.of(1L);
            ModuleName name = ModuleName.of("aggregate");
            ModuleId excludeId = ModuleId.of(1L);
            given(moduleQueryPort.existsByLayerIdAndNameExcluding(layerId, name, excludeId))
                    .willReturn(true);

            // when
            boolean result = sut.existsByLayerIdAndNameExcluding(layerId, name, excludeId);

            // then
            assertThat(result).isTrue();
            then(moduleQueryPort)
                    .should()
                    .existsByLayerIdAndNameExcluding(layerId, name, excludeId);
        }
    }

    @Nested
    @DisplayName("hasChildren 메서드")
    class HasChildren {

        @Test
        @DisplayName("성공 - 자식 모듈 존재 확인")
        void hasChildren_WhenChildrenExist_ShouldReturnTrue() {
            // given
            ModuleId parentId = ModuleId.of(1L);
            given(moduleQueryPort.existsByParentModuleId(parentId)).willReturn(true);

            // when
            boolean result = sut.hasChildren(parentId);

            // then
            assertThat(result).isTrue();
            then(moduleQueryPort).should().existsByParentModuleId(parentId);
        }
    }

    @Nested
    @DisplayName("searchByKeyword 메서드")
    class SearchByKeyword {

        @Test
        @DisplayName("성공 - 키워드로 모듈 검색")
        void searchByKeyword_WithKeyword_ShouldReturnList() {
            // given
            String keyword = "aggregate";
            Long layerId = 1L;
            List<Module> modules = List.of(module);
            given(moduleQueryPort.searchByKeyword(keyword, layerId)).willReturn(modules);

            // when
            List<Module> result = sut.searchByKeyword(keyword, layerId);

            // then
            assertThat(result).hasSize(1).containsExactly(module);
            then(moduleQueryPort).should().searchByKeyword(keyword, layerId);
        }
    }
}
