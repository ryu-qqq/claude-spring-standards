package com.ryuqq.application.layer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layer.assembler.LayerAssembler;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.application.layer.factory.query.LayerQueryFactory;
import com.ryuqq.application.layer.manager.LayerReadManager;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
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
 * SearchLayersByCursorService 단위 테스트
 *
 * <p>Layer 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchLayersByCursorService 단위 테스트")
class SearchLayersByCursorServiceTest {

    @Mock private LayerReadManager layerReadManager;

    @Mock private LayerQueryFactory layerQueryFactory;

    @Mock private LayerAssembler layerAssembler;

    @Mock private LayerSliceCriteria criteria;

    @Mock private Layer layer;

    @Mock private LayerSliceResult sliceResult;

    private SearchLayersByCursorService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchLayersByCursorService(layerReadManager, layerQueryFactory, layerAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 Layer 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            LayerSearchParams searchParams = createDefaultSearchParams();
            List<Layer> layers = List.of(layer);

            given(layerQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(layerReadManager.findBySliceCriteria(criteria)).willReturn(layers);
            given(layerAssembler.toSliceResult(layers, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            LayerSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(layerQueryFactory).should().createSliceCriteria(searchParams);
            then(layerReadManager).should().findBySliceCriteria(criteria);
            then(layerAssembler).should().toSliceResult(layers, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            LayerSearchParams searchParams = createDefaultSearchParams();
            List<Layer> emptyList = List.of();

            given(layerQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(layerReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(layerAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            LayerSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(layerQueryFactory).should().createSliceCriteria(searchParams);
            then(layerReadManager).should().findBySliceCriteria(criteria);
            then(layerAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private LayerSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return LayerSearchParams.of(cursorParams);
    }
}
