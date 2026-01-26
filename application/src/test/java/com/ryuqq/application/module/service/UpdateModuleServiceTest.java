package com.ryuqq.application.module.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import com.ryuqq.application.module.factory.command.ModuleCommandFactory;
import com.ryuqq.application.module.fixture.UpdateModuleCommandFixture;
import com.ryuqq.application.module.manager.ModulePersistenceManager;
import com.ryuqq.application.module.validator.ModuleValidator;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.aggregate.ModuleUpdateData;
import com.ryuqq.domain.module.exception.ModuleDuplicateNameException;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.module.fixture.ModuleFixture;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.ModuleName;
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
 * UpdateModuleService 단위 테스트
 *
 * <p>Module 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateModuleService 단위 테스트")
class UpdateModuleServiceTest {

    @Mock private ModuleValidator moduleValidator;

    @Mock private ModuleCommandFactory moduleCommandFactory;

    @Mock private ModulePersistenceManager modulePersistenceManager;

    @Mock private ModuleUpdateData updateData;

    private UpdateModuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateModuleService(
                        moduleValidator, moduleCommandFactory, modulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Module 수정")
        void execute_WithValidCommand_ShouldUpdateModule() {
            // given
            UpdateModuleCommand command = UpdateModuleCommandFixture.defaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ModuleName moduleName = ModuleName.of(command.name());
            Module module = ModuleFixture.defaultExistingModule();
            Instant changedAt = Instant.now();

            UpdateContext<ModuleId, ModuleUpdateData> context =
                    new UpdateContext<>(moduleId, updateData, changedAt);

            given(moduleCommandFactory.createUpdateContext(command)).willReturn(context);
            given(moduleValidator.findExistingOrThrow(moduleId)).willReturn(module);
            willDoNothing()
                    .given(moduleValidator)
                    .validateNotDuplicateExcluding(module.layerId(), moduleName, moduleId);

            // when
            sut.execute(command);

            // then
            then(moduleCommandFactory).should().createUpdateContext(command);
            then(moduleValidator).should().findExistingOrThrow(moduleId);
            then(moduleValidator)
                    .should()
                    .validateNotDuplicateExcluding(module.layerId(), moduleName, moduleId);
            then(modulePersistenceManager).should().persist(module);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Module인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateModuleCommand command = UpdateModuleCommandFixture.defaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            Instant changedAt = Instant.now();

            UpdateContext<ModuleId, ModuleUpdateData> context =
                    new UpdateContext<>(moduleId, updateData, changedAt);

            given(moduleCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ModuleNotFoundException(command.moduleId()))
                    .given(moduleValidator)
                    .findExistingOrThrow(moduleId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ModuleNotFoundException.class);

            then(modulePersistenceManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우")
        void execute_WhenNameDuplicate_ShouldThrowException() {
            // given
            UpdateModuleCommand command = UpdateModuleCommandFixture.defaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ModuleName moduleName = ModuleName.of(command.name());
            Module module = ModuleFixture.defaultExistingModule();
            Instant changedAt = Instant.now();

            UpdateContext<ModuleId, ModuleUpdateData> context =
                    new UpdateContext<>(moduleId, updateData, changedAt);

            given(moduleCommandFactory.createUpdateContext(command)).willReturn(context);
            given(moduleValidator.findExistingOrThrow(moduleId)).willReturn(module);
            willThrow(new ModuleDuplicateNameException(module.layerId(), moduleName))
                    .given(moduleValidator)
                    .validateNotDuplicateExcluding(module.layerId(), moduleName, moduleId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ModuleDuplicateNameException.class);

            then(modulePersistenceManager).shouldHaveNoInteractions();
        }
    }
}
