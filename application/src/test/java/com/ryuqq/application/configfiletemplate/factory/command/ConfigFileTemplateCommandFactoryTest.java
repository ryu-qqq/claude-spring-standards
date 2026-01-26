package com.ryuqq.application.configfiletemplate.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.dto.command.UpdateConfigFileTemplateCommand;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplateUpdateData;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
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
 * ConfigFileTemplateCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ConfigFileTemplateCommandFactory 단위 테스트")
class ConfigFileTemplateCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ConfigFileTemplateCommandFactory sut;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ConfigFileTemplateCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateCommand로 ConfigFileTemplate 생성")
        void create_WithValidCommand_ShouldReturnConfigFileTemplate() {
            // given
            CreateConfigFileTemplateCommand command = createDefaultCommand();
            given(timeProvider.now()).willReturn(NOW);

            // when
            ConfigFileTemplate result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackId().value()).isEqualTo(command.techStackId());
            assertThat(result.toolType()).isEqualTo(ToolType.CLAUDE);
            assertThat(result.filePath().value()).isEqualTo(command.filePath());
            assertThat(result.fileName().value()).isEqualTo(command.fileName());
            assertThat(result.content().value()).isEqualTo(command.content());
            assertThat(result.isRequired()).isEqualTo(command.isRequired());
        }

        @Test
        @DisplayName("성공 - architectureId가 null인 경우")
        void create_WithNullArchitectureId_ShouldReturnConfigFileTemplateWithNullArchitecture() {
            // given
            CreateConfigFileTemplateCommand command = createDefaultCommand();
            given(timeProvider.now()).willReturn(NOW);

            // when
            ConfigFileTemplate result = sut.create(command);

            // then
            assertThat(result.architectureId()).isNull();
        }
    }

    @Nested
    @DisplayName("createUpdateData 메서드")
    class CreateUpdateData {

        @Test
        @DisplayName("성공 - UpdateCommand로 UpdateData 생성")
        void createUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateConfigFileTemplateCommand command = createUpdateCommand();

            // when
            ConfigFileTemplateUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toolType()).isEqualTo(ToolType.CLAUDE);
            assertThat(result.filePath().value()).isEqualTo(command.filePath());
            assertThat(result.fileName().value()).isEqualTo(command.fileName());
            assertThat(result.content().value()).isEqualTo(command.content());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateConfigFileTemplateCommand command = createUpdateCommand();
            given(timeProvider.now()).willReturn(NOW);

            // when
            UpdateContext<ConfigFileTemplateId, ConfigFileTemplateUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(NOW);
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

    private UpdateConfigFileTemplateCommand createUpdateCommand() {
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
