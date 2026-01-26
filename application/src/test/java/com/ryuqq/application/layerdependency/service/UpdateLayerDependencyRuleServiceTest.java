package com.ryuqq.application.layerdependency.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.factory.command.LayerDependencyRuleCommandFactory;
import com.ryuqq.application.layerdependency.manager.LayerDependencyRulePersistenceManager;
import com.ryuqq.application.layerdependency.validator.LayerDependencyRuleValidator;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRuleUpdateData;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
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
 * UpdateLayerDependencyRuleService 단위 테스트
 *
 * <p>LayerDependencyRule 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateLayerDependencyRuleService 단위 테스트")
class UpdateLayerDependencyRuleServiceTest {

    @Mock private LayerDependencyRuleValidator layerDependencyRuleValidator;

    @Mock private LayerDependencyRuleCommandFactory layerDependencyRuleCommandFactory;

    @Mock private LayerDependencyRulePersistenceManager layerDependencyRulePersistenceManager;

    @Mock private LayerDependencyRule layerDependencyRule;

    @Mock private LayerDependencyRuleUpdateData updateData;

    private UpdateLayerDependencyRuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateLayerDependencyRuleService(
                        layerDependencyRuleValidator,
                        layerDependencyRuleCommandFactory,
                        layerDependencyRulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 LayerDependencyRule 수정")
        void execute_WithValidCommand_ShouldUpdateLayerDependencyRule() {
            // given
            UpdateLayerDependencyRuleCommand command = createDefaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.architectureId());
            LayerDependencyRuleId layerDependencyRuleId =
                    LayerDependencyRuleId.of(command.layerDependencyRuleId());
            Instant changedAt = Instant.now();
            UpdateContext<LayerDependencyRuleId, LayerDependencyRuleUpdateData> context =
                    new UpdateContext<>(layerDependencyRuleId, updateData, changedAt);

            willDoNothing()
                    .given(layerDependencyRuleValidator)
                    .validateArchitectureExists(architectureId);
            given(layerDependencyRuleCommandFactory.createUpdateContext(command))
                    .willReturn(context);
            given(layerDependencyRuleValidator.findExistingOrThrow(layerDependencyRuleId))
                    .willReturn(layerDependencyRule);
            willDoNothing().given(layerDependencyRule).update(updateData, changedAt);
            given(layerDependencyRulePersistenceManager.persist(layerDependencyRule))
                    .willReturn(layerDependencyRuleId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(layerDependencyRuleValidator).should().validateArchitectureExists(architectureId);
            then(layerDependencyRuleCommandFactory).should().createUpdateContext(command);
            then(layerDependencyRuleValidator).should().findExistingOrThrow(layerDependencyRuleId);
            then(layerDependencyRule).should().update(updateData, changedAt);
            then(layerDependencyRulePersistenceManager).should().persist(layerDependencyRule);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Architecture인 경우")
        void execute_WhenArchitectureNotFound_ShouldThrowException() {
            // given
            UpdateLayerDependencyRuleCommand command = createDefaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.architectureId());

            willThrow(new ArchitectureNotFoundException(architectureId.value()))
                    .given(layerDependencyRuleValidator)
                    .validateArchitectureExists(architectureId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchitectureNotFoundException.class);

            then(layerDependencyRulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateLayerDependencyRuleCommand createDefaultCommand() {
        return new UpdateLayerDependencyRuleCommand(
                1L, 1L, "DOMAIN", "APPLICATION", "FORBIDDEN", "도메인은 애플리케이션에 의존할 수 없습니다.");
    }
}
