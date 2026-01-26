package com.ryuqq.application.layer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.factory.command.LayerCommandFactory;
import com.ryuqq.application.layer.manager.LayerPersistenceManager;
import com.ryuqq.application.layer.validator.LayerValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.exception.LayerDuplicateCodeException;
import com.ryuqq.domain.layer.vo.LayerCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateLayerService 단위 테스트
 *
 * <p>Layer 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateLayerService 단위 테스트")
class CreateLayerServiceTest {

    @Mock private LayerValidator layerValidator;

    @Mock private LayerCommandFactory layerCommandFactory;

    @Mock private LayerPersistenceManager layerPersistenceManager;

    @Mock private Layer layer;

    private CreateLayerService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateLayerService(layerValidator, layerCommandFactory, layerPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Layer 생성")
        void execute_WithValidCommand_ShouldCreateLayer() {
            // given
            CreateLayerCommand command = createDefaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.architectureId());
            LayerCode layerCode = LayerCode.of(command.code());
            Long expectedId = 1L;

            willDoNothing()
                    .given(layerValidator)
                    .validateCodeNotDuplicated(architectureId, layerCode);
            given(layerCommandFactory.create(command)).willReturn(layer);
            given(layerPersistenceManager.persist(layer)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(layerValidator).should().validateCodeNotDuplicated(architectureId, layerCode);
            then(layerCommandFactory).should().create(command);
            then(layerPersistenceManager).should().persist(layer);
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            CreateLayerCommand command = createDefaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.architectureId());
            LayerCode layerCode = LayerCode.of(command.code());

            willThrow(new LayerDuplicateCodeException(command.code(), architectureId.value()))
                    .given(layerValidator)
                    .validateCodeNotDuplicated(architectureId, layerCode);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(LayerDuplicateCodeException.class);

            then(layerCommandFactory).shouldHaveNoInteractions();
            then(layerPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateLayerCommand createDefaultCommand() {
        return new CreateLayerCommand(1L, "DOMAIN", "Domain Layer", "Domain layer description", 1);
    }
}
