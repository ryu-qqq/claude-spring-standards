package com.ryuqq.application.resourcetemplate.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.factory.command.ResourceTemplateCommandFactory;
import com.ryuqq.application.resourcetemplate.manager.ResourceTemplatePersistenceManager;
import com.ryuqq.application.resourcetemplate.validator.ResourceTemplateValidator;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplateUpdateData;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateNotFoundException;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
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
 * UpdateResourceTemplateService 단위 테스트
 *
 * <p>ResourceTemplate 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateResourceTemplateService 단위 테스트")
class UpdateResourceTemplateServiceTest {

    @Mock private ResourceTemplateCommandFactory resourceTemplateCommandFactory;

    @Mock private ResourceTemplateValidator resourceTemplateValidator;

    @Mock private ResourceTemplatePersistenceManager resourceTemplatePersistenceManager;

    @Mock private ResourceTemplate resourceTemplate;

    @Mock private ResourceTemplateUpdateData updateData;

    private UpdateResourceTemplateService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateResourceTemplateService(
                        resourceTemplateCommandFactory,
                        resourceTemplateValidator,
                        resourceTemplatePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ResourceTemplate 수정")
        void execute_WithValidCommand_ShouldUpdateResourceTemplate() {
            // given
            UpdateResourceTemplateCommand command = createDefaultCommand();
            ResourceTemplateId resourceTemplateId =
                    ResourceTemplateId.of(command.resourceTemplateId());
            Instant changedAt = Instant.now();
            UpdateContext<ResourceTemplateId, ResourceTemplateUpdateData> context =
                    new UpdateContext<>(resourceTemplateId, updateData, changedAt);

            given(resourceTemplateCommandFactory.createUpdateContext(command)).willReturn(context);
            given(resourceTemplateValidator.findExistingOrThrow(resourceTemplateId))
                    .willReturn(resourceTemplate);
            willDoNothing().given(resourceTemplate).update(updateData, changedAt);
            given(resourceTemplatePersistenceManager.persist(resourceTemplate))
                    .willReturn(resourceTemplateId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(resourceTemplateCommandFactory).should().createUpdateContext(command);
            then(resourceTemplateValidator).should().findExistingOrThrow(resourceTemplateId);
            then(resourceTemplate).should().update(updateData, changedAt);
            then(resourceTemplatePersistenceManager).should().persist(resourceTemplate);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ResourceTemplate인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateResourceTemplateCommand command = createDefaultCommand();
            ResourceTemplateId resourceTemplateId =
                    ResourceTemplateId.of(command.resourceTemplateId());
            Instant changedAt = Instant.now();
            UpdateContext<ResourceTemplateId, ResourceTemplateUpdateData> context =
                    new UpdateContext<>(resourceTemplateId, updateData, changedAt);

            given(resourceTemplateCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ResourceTemplateNotFoundException(resourceTemplateId.value()))
                    .given(resourceTemplateValidator)
                    .findExistingOrThrow(resourceTemplateId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ResourceTemplateNotFoundException.class);

            then(resourceTemplatePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateResourceTemplateCommand createDefaultCommand() {
        return new UpdateResourceTemplateCommand(
                1L,
                "CONFIG",
                "application.yml",
                "YAML",
                "Updated description",
                "spring:\n  application:\n    name: updated",
                true);
    }
}
