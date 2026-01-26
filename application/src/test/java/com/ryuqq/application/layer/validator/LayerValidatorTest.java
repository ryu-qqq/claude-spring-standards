package com.ryuqq.application.layer.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.layer.manager.LayerReadManager;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.exception.LayerDuplicateCodeException;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import com.ryuqq.domain.layer.fixture.LayerFixture;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
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
 * LayerValidator 단위 테스트
 *
 * <p>Layer 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("LayerValidator 단위 테스트")
class LayerValidatorTest {

    @Mock private LayerReadManager layerReadManager;

    private LayerValidator sut;

    @BeforeEach
    void setUp() {
        sut = new LayerValidator(layerReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 Layer 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnLayer() {
            // given
            LayerId id = LayerId.of(1L);
            Layer expected = LayerFixture.defaultExistingLayer();

            given(layerReadManager.findById(id)).willReturn(Optional.of(expected));

            // when
            Layer result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            LayerId id = LayerId.of(999L);

            given(layerReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(LayerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicated 메서드")
    class ValidateCodeNotDuplicated {

        @Test
        @DisplayName("성공 - 중복되지 않는 코드")
        void validateCodeNotDuplicated_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("NEW_LAYER");

            given(layerReadManager.existsByArchitectureIdAndCode(architectureId, code))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateCodeNotDuplicated(architectureId, code))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우 예외")
        void validateCodeNotDuplicated_WhenDuplicate_ShouldThrowException() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("DOMAIN");

            given(layerReadManager.existsByArchitectureIdAndCode(architectureId, code))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateCodeNotDuplicated(architectureId, code))
                    .isInstanceOf(LayerDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicatedExcluding 메서드")
    class ValidateCodeNotDuplicatedExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 코드")
        void validateCodeNotDuplicatedExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("UPDATED_LAYER");
            LayerId excludeId = LayerId.of(1L);

            given(
                            layerReadManager.existsByArchitectureIdAndCodeAndIdNot(
                                    architectureId, code, excludeId))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(
                            () ->
                                    sut.validateCodeNotDuplicatedExcluding(
                                            architectureId, code, excludeId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 Layer에서 이미 사용 중인 경우 예외")
        void validateCodeNotDuplicatedExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("DOMAIN");
            LayerId excludeId = LayerId.of(1L);

            given(
                            layerReadManager.existsByArchitectureIdAndCodeAndIdNot(
                                    architectureId, code, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateCodeNotDuplicatedExcluding(
                                            architectureId, code, excludeId))
                    .isInstanceOf(LayerDuplicateCodeException.class);
        }
    }
}
