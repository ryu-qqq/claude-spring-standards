package com.ryuqq.application.layerdependency.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.factory.command.LayerDependencyRuleCommandFactory;
import com.ryuqq.application.layerdependency.manager.LayerDependencyRulePersistenceManager;
import com.ryuqq.application.layerdependency.validator.LayerDependencyRuleValidator;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateLayerDependencyRuleService 단위 테스트
 *
 * <p>LayerDependencyRule 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateLayerDependencyRuleService 단위 테스트")
class CreateLayerDependencyRuleServiceTest {

    @Mock private LayerDependencyRuleValidator layerDependencyRuleValidator;

    @Mock private LayerDependencyRuleCommandFactory layerDependencyRuleCommandFactory;

    @Mock private LayerDependencyRulePersistenceManager layerDependencyRulePersistenceManager;

    @Mock private LayerDependencyRule layerDependencyRule;

    private CreateLayerDependencyRuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateLayerDependencyRuleService(
                        layerDependencyRuleValidator,
                        layerDependencyRuleCommandFactory,
                        layerDependencyRulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 LayerDependencyRule 생성")
        void execute_WithValidCommand_ShouldCreateLayerDependencyRule() {
            // given
            CreateLayerDependencyRuleCommand command = createDefaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.architectureId());
            LayerDependencyRuleId savedId = LayerDependencyRuleId.of(1L);

            willDoNothing()
                    .given(layerDependencyRuleValidator)
                    .validateArchitectureExists(architectureId);
            given(layerDependencyRuleCommandFactory.create(command))
                    .willReturn(layerDependencyRule);
            given(layerDependencyRulePersistenceManager.persist(layerDependencyRule))
                    .willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(layerDependencyRuleValidator).should().validateArchitectureExists(architectureId);
            then(layerDependencyRuleCommandFactory).should().create(command);
            then(layerDependencyRulePersistenceManager).should().persist(layerDependencyRule);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Architecture인 경우")
        void execute_WhenArchitectureNotFound_ShouldThrowException() {
            // given
            CreateLayerDependencyRuleCommand command = createDefaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.architectureId());

            willThrow(new ArchitectureNotFoundException(architectureId.value()))
                    .given(layerDependencyRuleValidator)
                    .validateArchitectureExists(architectureId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchitectureNotFoundException.class);

            then(layerDependencyRuleCommandFactory).shouldHaveNoInteractions();
            then(layerDependencyRulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateLayerDependencyRuleCommand createDefaultCommand() {
        return new CreateLayerDependencyRuleCommand(
                1L, "DOMAIN", "APPLICATION", "FORBIDDEN", "도메인은 애플리케이션에 의존할 수 없습니다.");
    }
}
