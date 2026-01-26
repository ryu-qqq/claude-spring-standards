package com.ryuqq.application.configfiletemplate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.factory.command.ConfigFileTemplateCommandFactory;
import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplatePersistenceManager;
import com.ryuqq.application.configfiletemplate.validator.ConfigFileTemplateValidator;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateConfigFileTemplateService 단위 테스트
 *
 * <p>ConfigFileTemplate 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateConfigFileTemplateService 단위 테스트")
class CreateConfigFileTemplateServiceTest {

    @Mock private ConfigFileTemplateValidator configFileTemplateValidator;

    @Mock private ConfigFileTemplateCommandFactory configFileTemplateCommandFactory;

    @Mock private ConfigFileTemplatePersistenceManager configFileTemplatePersistenceManager;

    @Mock private ConfigFileTemplate configFileTemplate;

    private CreateConfigFileTemplateService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateConfigFileTemplateService(
                        configFileTemplateValidator,
                        configFileTemplateCommandFactory,
                        configFileTemplatePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ConfigFileTemplate 생성")
        void execute_WithValidCommand_ShouldCreateConfigFileTemplate() {
            // given
            CreateConfigFileTemplateCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());
            Long savedId = 1L;

            willDoNothing().given(configFileTemplateValidator).validateTechStackExists(techStackId);
            given(configFileTemplateCommandFactory.create(command)).willReturn(configFileTemplate);
            given(configFileTemplatePersistenceManager.persist(configFileTemplate))
                    .willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId);

            then(configFileTemplateValidator).should().validateTechStackExists(techStackId);
            then(configFileTemplateCommandFactory).should().create(command);
            then(configFileTemplatePersistenceManager).should().persist(configFileTemplate);
        }

        @Test
        @DisplayName("실패 - TechStack이 존재하지 않는 경우")
        void execute_WhenTechStackNotExists_ShouldThrowException() {
            // given
            CreateConfigFileTemplateCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());

            willThrow(new TechStackNotFoundException(techStackId.value()))
                    .given(configFileTemplateValidator)
                    .validateTechStackExists(techStackId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TechStackNotFoundException.class);

            then(configFileTemplateCommandFactory).shouldHaveNoInteractions();
            then(configFileTemplatePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateConfigFileTemplateCommand createDefaultCommand() {
        return new CreateConfigFileTemplateCommand(
                1L,
                null,
                "CLAUDE",
                ".claude/CLAUDE.md",
                "CLAUDE.md",
                "# Project Configuration",
                "MAIN_CONFIG",
                "Claude Code 메인 설정 파일",
                null,
                0,
                true);
    }
}
