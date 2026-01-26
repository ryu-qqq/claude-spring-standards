package com.ryuqq.application.layer.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.layer.dto.response.LayerResult;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.fixture.LayerFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LayerAssembler 단위 테스트
 *
 * <p>Layer 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("LayerAssembler 단위 테스트")
class LayerAssemblerTest {

    private LayerAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new LayerAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - Layer를 LayerResult로 변환")
        void toResult_WithValidLayer_ShouldReturnResult() {
            // given
            Layer layer = LayerFixture.defaultExistingLayer();

            // when
            LayerResult result = sut.toResult(layer);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(layer.idValue());
            assertThat(result.architectureId()).isEqualTo(layer.architectureIdValue());
            assertThat(result.code()).isEqualTo(layer.codeValue());
            assertThat(result.name()).isEqualTo(layer.nameValue());
            assertThat(result.description()).isEqualTo(layer.description());
            assertThat(result.orderIndex()).isEqualTo(layer.orderIndex());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - Layer 목록을 LayerResult 목록으로 변환")
        void toResults_WithValidLayers_ShouldReturnResults() {
            // given
            Layer layer1 = LayerFixture.defaultExistingLayer();
            Layer layer2 = LayerFixture.defaultExistingLayer();
            List<Layer> layers = List.of(layer1, layer2);

            // when
            List<LayerResult> results = sut.toResults(layers);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<Layer> layers = List.of();

            // when
            List<LayerResult> results = sut.toResults(layers);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            Layer layer1 = LayerFixture.defaultExistingLayer();
            Layer layer2 = LayerFixture.defaultExistingLayer();
            Layer layer3 = LayerFixture.defaultExistingLayer();
            List<Layer> layers = List.of(layer1, layer2, layer3);
            int size = 2;

            // when
            LayerSliceResult result = sut.toSliceResult(layers, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            Layer layer1 = LayerFixture.defaultExistingLayer();
            List<Layer> layers = List.of(layer1);
            int size = 10;

            // when
            LayerSliceResult result = sut.toSliceResult(layers, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}
