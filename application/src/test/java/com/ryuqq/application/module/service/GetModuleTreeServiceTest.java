package com.ryuqq.application.module.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.module.assembler.ModuleAssembler;
import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import com.ryuqq.application.module.manager.ModuleReadManager;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetModuleTreeService 단위 테스트
 *
 * <p>Module 트리 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("GetModuleTreeService 단위 테스트")
class GetModuleTreeServiceTest {

    @Mock private ModuleAssembler moduleAssembler;

    @Mock private ModuleReadManager moduleReadManager;

    @Mock private Module module;

    @Mock private ModuleTreeResult moduleTreeResult;

    private GetModuleTreeService sut;

    @BeforeEach
    void setUp() {
        sut = new GetModuleTreeService(moduleAssembler, moduleReadManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 LayerId로 Module 트리 조회")
        void execute_WithValidLayerId_ShouldReturnModuleTreeResults() {
            // given
            Long layerId = 1L;
            LayerId layerIdVo = LayerId.of(layerId);
            List<Module> modules = List.of(module);
            List<ModuleTreeResult> treeResults = List.of(moduleTreeResult);

            given(moduleReadManager.findAllByLayerId(layerIdVo)).willReturn(modules);
            given(moduleAssembler.toTreeResults(modules)).willReturn(treeResults);

            // when
            List<ModuleTreeResult> result = sut.execute(layerId);

            // then
            assertThat(result).isEqualTo(treeResults);

            then(moduleReadManager).should().findAllByLayerId(layerIdVo);
            then(moduleAssembler).should().toTreeResults(modules);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoModules_ShouldReturnEmptyList() {
            // given
            Long layerId = 1L;
            LayerId layerIdVo = LayerId.of(layerId);
            List<Module> emptyList = List.of();
            List<ModuleTreeResult> emptyResults = List.of();

            given(moduleReadManager.findAllByLayerId(layerIdVo)).willReturn(emptyList);
            given(moduleAssembler.toTreeResults(emptyList)).willReturn(emptyResults);

            // when
            List<ModuleTreeResult> result = sut.execute(layerId);

            // then
            assertThat(result).isEmpty();

            then(moduleReadManager).should().findAllByLayerId(layerIdVo);
            then(moduleAssembler).should().toTreeResults(emptyList);
        }
    }
}
