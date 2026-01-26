package com.ryuqq.application.module.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.factory.command.ModuleCommandFactory;
import com.ryuqq.application.module.manager.ModulePersistenceManager;
import com.ryuqq.application.module.validator.ModuleValidator;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.exception.ModuleDuplicateNameException;
import com.ryuqq.domain.module.vo.ModuleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateModuleService 단위 테스트
 *
 * <p>Module 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateModuleService 단위 테스트")
class CreateModuleServiceTest {

    @Mock private ModuleValidator moduleValidator;

    @Mock private ModuleCommandFactory moduleCommandFactory;

    @Mock private ModulePersistenceManager modulePersistenceManager;

    @Mock private Module module;

    private CreateModuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateModuleService(
                        moduleValidator, moduleCommandFactory, modulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Module 생성")
        void execute_WithValidCommand_ShouldCreateModule() {
            // given
            CreateModuleCommand command = createDefaultCommand();
            LayerId layerId = LayerId.of(command.layerId());
            ModuleName moduleName = ModuleName.of(command.name());
            Long expectedId = 1L;

            willDoNothing().given(moduleValidator).validateNotDuplicate(layerId, moduleName);
            given(moduleCommandFactory.create(command)).willReturn(module);
            given(modulePersistenceManager.persist(module)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(moduleValidator).should().validateNotDuplicate(layerId, moduleName);
            then(moduleCommandFactory).should().create(command);
            then(modulePersistenceManager).should().persist(module);
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우")
        void execute_WhenNameDuplicate_ShouldThrowException() {
            // given
            CreateModuleCommand command = createDefaultCommand();
            LayerId layerId = LayerId.of(command.layerId());
            ModuleName moduleName = ModuleName.of(command.name());

            willThrow(new ModuleDuplicateNameException(layerId, moduleName))
                    .given(moduleValidator)
                    .validateNotDuplicate(layerId, moduleName);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ModuleDuplicateNameException.class);

            then(moduleCommandFactory).shouldHaveNoInteractions();
            then(modulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateModuleCommand createDefaultCommand() {
        return new CreateModuleCommand(1L, null, "domain", "Domain module", "domain", ":domain");
    }
}
