package com.ryuqq.application.module.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.module.assembler.ModuleAssembler;
import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.factory.query.ModuleQueryFactory;
import com.ryuqq.application.module.manager.ModuleReadManager;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
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
 * SearchModulesByCursorService 단위 테스트
 *
 * <p>Module 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchModulesByCursorService 단위 테스트")
class SearchModulesByCursorServiceTest {

    @Mock private ModuleReadManager moduleReadManager;

    @Mock private ModuleQueryFactory moduleQueryFactory;

    @Mock private ModuleAssembler moduleAssembler;

    @Mock private ModuleSliceCriteria criteria;

    @Mock private Module module;

    @Mock private ModuleSliceResult sliceResult;

    private SearchModulesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchModulesByCursorService(
                        moduleReadManager, moduleQueryFactory, moduleAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 Module 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ModuleSearchParams searchParams = createDefaultSearchParams();
            List<Module> modules = List.of(module);

            given(moduleQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(moduleReadManager.findBySliceCriteria(criteria)).willReturn(modules);
            given(moduleAssembler.toSliceResult(modules, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ModuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(moduleQueryFactory).should().createSliceCriteria(searchParams);
            then(moduleReadManager).should().findBySliceCriteria(criteria);
            then(moduleAssembler).should().toSliceResult(modules, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ModuleSearchParams searchParams = createDefaultSearchParams();
            List<Module> emptyList = List.of();

            given(moduleQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(moduleReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(moduleAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ModuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(moduleQueryFactory).should().createSliceCriteria(searchParams);
            then(moduleReadManager).should().findBySliceCriteria(criteria);
            then(moduleAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ModuleSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ModuleSearchParams.of(cursorParams);
    }
}
