package com.ryuqq.application.configfiletemplate.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateResult;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.fixture.ConfigFileTemplateFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ConfigFileTemplateAssembler 단위 테스트
 *
 * <p>Domain → Response DTO 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ConfigFileTemplateAssembler 단위 테스트")
class ConfigFileTemplateAssemblerTest {

    private ConfigFileTemplateAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ConfigFileTemplateAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ConfigFileTemplate을 ConfigFileTemplateResult로 변환")
        void toResult_WithConfigFileTemplate_ShouldReturnResult() {
            // given
            ConfigFileTemplate configFileTemplate =
                    ConfigFileTemplateFixture.defaultExistingConfigFileTemplate();

            // when
            ConfigFileTemplateResult result = sut.toResult(configFileTemplate);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(configFileTemplate.idValue());
            assertThat(result.techStackId()).isEqualTo(configFileTemplate.techStackIdValue());
            assertThat(result.toolType()).isEqualTo(configFileTemplate.toolTypeName());
            assertThat(result.filePath()).isEqualTo(configFileTemplate.filePathValue());
            assertThat(result.fileName()).isEqualTo(configFileTemplate.fileNameValue());
            assertThat(result.content()).isEqualTo(configFileTemplate.contentValue());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - ConfigFileTemplate 목록을 Result 목록으로 변환")
        void toResults_WithConfigFileTemplates_ShouldReturnResults() {
            // given
            ConfigFileTemplate configFileTemplate1 =
                    ConfigFileTemplateFixture.defaultExistingConfigFileTemplate();
            ConfigFileTemplate configFileTemplate2 =
                    ConfigFileTemplateFixture.cursorConfigFileTemplate();
            List<ConfigFileTemplate> configFileTemplates =
                    List.of(configFileTemplate1, configFileTemplate2);

            // when
            List<ConfigFileTemplateResult> results = sut.toResults(configFileTemplates);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).id()).isEqualTo(configFileTemplate1.idValue());
            assertThat(results.get(1).id()).isEqualTo(configFileTemplate2.idValue());
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoMoreData_ShouldReturnSliceWithoutNext() {
            // given
            List<ConfigFileTemplate> configFileTemplates =
                    List.of(ConfigFileTemplateFixture.defaultExistingConfigFileTemplate());
            int size = 20;

            // when
            ConfigFileTemplateSliceResult result = sut.toSliceResult(configFileTemplates, size);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.sliceMeta().hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenMoreDataExists_ShouldReturnSliceWithNext() {
            // given
            List<ConfigFileTemplate> configFileTemplates =
                    List.of(
                            ConfigFileTemplateFixture.defaultExistingConfigFileTemplate(),
                            ConfigFileTemplateFixture.cursorConfigFileTemplate(),
                            ConfigFileTemplateFixture.copilotConfigFileTemplate());
            int size = 2;

            // when
            ConfigFileTemplateSliceResult result = sut.toSliceResult(configFileTemplates, size);

            // then
            assertThat(result.content()).hasSize(2);
            assertThat(result.sliceMeta().hasNext()).isTrue();
        }
    }
}
