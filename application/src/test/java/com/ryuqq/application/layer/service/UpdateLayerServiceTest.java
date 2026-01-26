package com.ryuqq.application.layer.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import com.ryuqq.application.layer.factory.command.LayerCommandFactory;
import com.ryuqq.application.layer.fixture.UpdateLayerCommandFixture;
import com.ryuqq.application.layer.manager.LayerPersistenceManager;
import com.ryuqq.application.layer.validator.LayerValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.aggregate.LayerUpdateData;
import com.ryuqq.domain.layer.exception.LayerDuplicateCodeException;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import com.ryuqq.domain.layer.fixture.LayerFixture;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateLayerService 단위 테스트
 *
 * <p>Layer 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateLayerService 단위 테스트")
class UpdateLayerServiceTest {

    @Mock private LayerValidator layerValidator;

    @Mock private LayerCommandFactory layerCommandFactory;

    @Mock private LayerPersistenceManager layerPersistenceManager;

    @Mock private LayerUpdateData updateData;

    private UpdateLayerService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdateLayerService(layerValidator, layerCommandFactory, layerPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Layer 수정")
        void execute_WithValidCommand_ShouldUpdateLayer() {
            // given
            UpdateLayerCommand command = UpdateLayerCommandFixture.defaultCommand();
            LayerId layerId = LayerId.of(command.id());
            LayerCode layerCode = LayerCode.of(command.code());
            Layer layer = LayerFixture.defaultExistingLayer();
            ArchitectureId architectureId = ArchitectureId.of(layer.architectureIdValue());
            Instant changedAt = Instant.now();

            UpdateContext<LayerId, LayerUpdateData> context =
                    new UpdateContext<>(layerId, updateData, changedAt);

            given(layerCommandFactory.createUpdateContext(command)).willReturn(context);
            given(layerValidator.findExistingOrThrow(layerId)).willReturn(layer);
            willDoNothing()
                    .given(layerValidator)
                    .validateCodeNotDuplicatedExcluding(architectureId, layerCode, layerId);

            // when
            sut.execute(command);

            // then
            then(layerCommandFactory).should().createUpdateContext(command);
            then(layerValidator).should().findExistingOrThrow(layerId);
            then(layerValidator)
                    .should()
                    .validateCodeNotDuplicatedExcluding(architectureId, layerCode, layerId);
            then(layerPersistenceManager).should().persist(layer);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Layer인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateLayerCommand command = UpdateLayerCommandFixture.defaultCommand();
            LayerId layerId = LayerId.of(command.id());
            Instant changedAt = Instant.now();

            UpdateContext<LayerId, LayerUpdateData> context =
                    new UpdateContext<>(layerId, updateData, changedAt);

            given(layerCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new LayerNotFoundException(command.id()))
                    .given(layerValidator)
                    .findExistingOrThrow(layerId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(LayerNotFoundException.class);

            then(layerPersistenceManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            UpdateLayerCommand command = UpdateLayerCommandFixture.defaultCommand();
            LayerId layerId = LayerId.of(command.id());
            LayerCode layerCode = LayerCode.of(command.code());
            Layer layer = LayerFixture.defaultExistingLayer();
            ArchitectureId architectureId = ArchitectureId.of(layer.architectureIdValue());
            Instant changedAt = Instant.now();

            UpdateContext<LayerId, LayerUpdateData> context =
                    new UpdateContext<>(layerId, updateData, changedAt);

            given(layerCommandFactory.createUpdateContext(command)).willReturn(context);
            given(layerValidator.findExistingOrThrow(layerId)).willReturn(layer);
            willThrow(new LayerDuplicateCodeException(command.code(), layer.architectureIdValue()))
                    .given(layerValidator)
                    .validateCodeNotDuplicatedExcluding(architectureId, layerCode, layerId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(LayerDuplicateCodeException.class);

            then(layerPersistenceManager).shouldHaveNoInteractions();
        }
    }
}
