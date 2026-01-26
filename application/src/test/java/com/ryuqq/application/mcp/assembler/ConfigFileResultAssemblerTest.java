package com.ryuqq.application.mcp.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.mcp.dto.response.ConfigFileResult;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.fixture.ConfigFileTemplateFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ConfigFileResultAssembler 단위 테스트
 *
 * <p>ConfigFileTemplate → MCP Result DTO 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ConfigFileResultAssembler 단위 테스트")
class ConfigFileResultAssemblerTest {

    private ConfigFileResultAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ConfigFileResultAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ConfigFileTemplate을 ConfigFileResult로 변환")
        void toResult_WithValidTemplate_ShouldReturnResult() {
            // given
            ConfigFileTemplate template =
                    ConfigFileTemplateFixture.defaultExistingConfigFileTemplate();

            // when
            ConfigFileResult result = sut.toResult(template);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(template.idValue());
            assertThat(result.toolType()).isEqualTo(template.toolTypeName());
            assertThat(result.filePath()).isEqualTo(template.filePathValue());
            assertThat(result.fileName()).isEqualTo(template.fileNameValue());
            assertThat(result.description()).isEqualTo(template.descriptionValue());
            assertThat(result.templateContent()).isEqualTo(template.contentValue());
            assertThat(result.priority()).isEqualTo(template.displayOrderValue());
        }

        @Test
        @DisplayName("성공 - Cursor 타입 ConfigFileTemplate 변환")
        void toResult_WithCursorTemplate_ShouldReturnResult() {
            // given
            ConfigFileTemplate template = ConfigFileTemplateFixture.cursorConfigFileTemplate();

            // when
            ConfigFileResult result = sut.toResult(template);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toolType()).isEqualTo("CURSOR");
            assertThat(result.filePath()).isEqualTo(".cursor/");
            assertThat(result.fileName()).isEqualTo("rules.json");
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - ConfigFileTemplate 목록을 ConfigFileResult 목록으로 변환")
        void toResults_WithValidTemplates_ShouldReturnResults() {
            // given
            ConfigFileTemplate template1 =
                    ConfigFileTemplateFixture.defaultExistingConfigFileTemplate();
            ConfigFileTemplate template2 = ConfigFileTemplateFixture.cursorConfigFileTemplate();
            List<ConfigFileTemplate> templates = List.of(template1, template2);

            // when
            List<ConfigFileResult> results = sut.toResults(templates);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).toolType()).isEqualTo("CLAUDE");
            assertThat(results.get(1).toolType()).isEqualTo("CURSOR");
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<ConfigFileTemplate> templates = List.of();

            // when
            List<ConfigFileResult> results = sut.toResults(templates);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toConfigFilesResult 메서드")
    class ToConfigFilesResult {

        @Test
        @DisplayName("성공 - ConfigFileTemplate 목록을 ConfigFilesResult로 변환")
        void toConfigFilesResult_WithValidTemplates_ShouldReturnResult() {
            // given
            ConfigFileTemplate template1 =
                    ConfigFileTemplateFixture.defaultExistingConfigFileTemplate();
            ConfigFileTemplate template2 = ConfigFileTemplateFixture.cursorConfigFileTemplate();
            ConfigFileTemplate template3 = ConfigFileTemplateFixture.copilotConfigFileTemplate();
            List<ConfigFileTemplate> templates = List.of(template1, template2, template3);

            // when
            ConfigFilesResult result = sut.toConfigFilesResult(templates);

            // then
            assertThat(result).isNotNull();
            assertThat(result.configFiles()).hasSize(3);
            assertThat(result.totalCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toConfigFilesResult_WithEmptyList_ShouldReturnEmptyResult() {
            // given
            List<ConfigFileTemplate> templates = List.of();

            // when
            ConfigFilesResult result = sut.toConfigFilesResult(templates);

            // then
            assertThat(result).isNotNull();
            assertThat(result.configFiles()).isEmpty();
            assertThat(result.totalCount()).isZero();
        }
    }
}
