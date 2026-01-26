package com.ryuqq.application.configfiletemplate.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.configfiletemplate.dto.command.UpdateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.factory.command.ConfigFileTemplateCommandFactory;
import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplatePersistenceManager;
import com.ryuqq.application.configfiletemplate.validator.ConfigFileTemplateValidator;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplateUpdateData;
import com.ryuqq.domain.configfiletemplate.exception.ConfigFileTemplateNotFoundException;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
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
 * UpdateConfigFileTemplateService 단위 테스트
 *
 * <p>ConfigFileTemplate 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateConfigFileTemplateService 단위 테스트")
class UpdateConfigFileTemplateServiceTest {

    @Mock private ConfigFileTemplateValidator configFileTemplateValidator;

    @Mock private ConfigFileTemplateCommandFactory configFileTemplateCommandFactory;

    @Mock private ConfigFileTemplatePersistenceManager configFileTemplatePersistenceManager;

    @Mock private ConfigFileTemplate configFileTemplate;

    @Mock private ConfigFileTemplateUpdateData updateData;

    private UpdateConfigFileTemplateService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateConfigFileTemplateService(
                        configFileTemplateValidator,
                        configFileTemplateCommandFactory,
                        configFileTemplatePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ConfigFileTemplate 수정")
        void execute_WithValidCommand_ShouldUpdateConfigFileTemplate() {
            // given
            UpdateConfigFileTemplateCommand command = createDefaultCommand();
            ConfigFileTemplateId id = ConfigFileTemplateId.of(command.id());
            Instant now = Instant.now();
            UpdateContext<ConfigFileTemplateId, ConfigFileTemplateUpdateData> context =
                    new UpdateContext<>(id, updateData, now);

            given(configFileTemplateCommandFactory.createUpdateContext(command))
                    .willReturn(context);
            given(configFileTemplateValidator.findExistingOrThrow(id))
                    .willReturn(configFileTemplate);
            given(configFileTemplatePersistenceManager.persist(configFileTemplate)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(configFileTemplateCommandFactory).should().createUpdateContext(command);
            then(configFileTemplateValidator).should().findExistingOrThrow(id);
            then(configFileTemplate).should().update(updateData, now);
            then(configFileTemplatePersistenceManager).should().persist(configFileTemplate);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ConfigFileTemplate 수정 시도")
        void execute_WhenConfigFileTemplateNotExists_ShouldThrowException() {
            // given
            UpdateConfigFileTemplateCommand command = createDefaultCommand();
            ConfigFileTemplateId id = ConfigFileTemplateId.of(command.id());
            UpdateContext<ConfigFileTemplateId, ConfigFileTemplateUpdateData> context =
                    new UpdateContext<>(id, updateData, Instant.now());

            given(configFileTemplateCommandFactory.createUpdateContext(command))
                    .willReturn(context);
            willThrow(new ConfigFileTemplateNotFoundException(id.value()))
                    .given(configFileTemplateValidator)
                    .findExistingOrThrow(id);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ConfigFileTemplateNotFoundException.class);

            then(configFileTemplatePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateConfigFileTemplateCommand createDefaultCommand() {
        return new UpdateConfigFileTemplateCommand(
                1L,
                "CLAUDE",
                ".claude/CLAUDE.md",
                "CLAUDE.md",
                "# Updated Project Configuration",
                "MAIN_CONFIG",
                "Claude Code 메인 설정 파일 (수정됨)",
                null,
                0,
                true);
    }
}
