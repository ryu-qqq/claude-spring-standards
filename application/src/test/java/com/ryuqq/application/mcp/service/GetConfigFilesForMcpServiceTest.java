package com.ryuqq.application.mcp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplateReadManager;
import com.ryuqq.application.mcp.assembler.ConfigFileResultAssembler;
import com.ryuqq.application.mcp.dto.query.GetConfigFilesQuery;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetConfigFilesForMcpService 단위 테스트
 *
 * <p>MCP init_project Tool용 Config Files 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("GetConfigFilesForMcpService 단위 테스트")
class GetConfigFilesForMcpServiceTest {

    @Mock private ConfigFileTemplateReadManager configFileTemplateReadManager;

    @Mock private ConfigFileResultAssembler configFileResultAssembler;

    @Mock private ConfigFileTemplate configFileTemplate;

    @Mock private ConfigFilesResult configFilesResult;

    private GetConfigFilesForMcpService sut;

    @BeforeEach
    void setUp() {
        sut =
                new GetConfigFilesForMcpService(
                        configFileTemplateReadManager, configFileResultAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Query로 Config Files 조회")
        void execute_WithValidQuery_ShouldReturnResult() {
            // given
            GetConfigFilesQuery query =
                    new GetConfigFilesQuery(List.of("CLAUDE", "CURSOR"), 1L, 1L);
            List<ConfigFileTemplate> templates = List.of(configFileTemplate);
            List<ToolType> toolTypes = List.of(ToolType.CLAUDE, ToolType.CURSOR);

            given(configFileTemplateReadManager.findForMcp(TechStackId.of(1L), 1L, toolTypes))
                    .willReturn(templates);
            given(configFileResultAssembler.toConfigFilesResult(templates))
                    .willReturn(configFilesResult);

            // when
            ConfigFilesResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(configFilesResult);

            then(configFileTemplateReadManager)
                    .should()
                    .findForMcp(TechStackId.of(1L), 1L, toolTypes);
            then(configFileResultAssembler).should().toConfigFilesResult(templates);
        }

        @Test
        @DisplayName("성공 - toolTypes가 null인 경우")
        void execute_WithNullToolTypes_ShouldReturnResult() {
            // given
            GetConfigFilesQuery query = new GetConfigFilesQuery(null, 1L, 1L);
            List<ConfigFileTemplate> templates = List.of(configFileTemplate);

            given(configFileTemplateReadManager.findForMcp(TechStackId.of(1L), 1L, null))
                    .willReturn(templates);
            given(configFileResultAssembler.toConfigFilesResult(templates))
                    .willReturn(configFilesResult);

            // when
            ConfigFilesResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(configFilesResult);

            then(configFileTemplateReadManager).should().findForMcp(TechStackId.of(1L), 1L, null);
            then(configFileResultAssembler).should().toConfigFilesResult(templates);
        }

        @Test
        @DisplayName("성공 - toolTypes가 빈 목록인 경우")
        void execute_WithEmptyToolTypes_ShouldReturnResult() {
            // given
            GetConfigFilesQuery query = new GetConfigFilesQuery(List.of(), 1L, null);
            List<ConfigFileTemplate> templates = List.of();

            given(configFileTemplateReadManager.findForMcp(TechStackId.of(1L), null, null))
                    .willReturn(templates);
            given(configFileResultAssembler.toConfigFilesResult(templates))
                    .willReturn(configFilesResult);

            // when
            ConfigFilesResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(configFilesResult);

            then(configFileTemplateReadManager).should().findForMcp(TechStackId.of(1L), null, null);
            then(configFileResultAssembler).should().toConfigFilesResult(templates);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptyResult() {
            // given
            GetConfigFilesQuery query = new GetConfigFilesQuery(List.of("CLAUDE"), 1L, 1L);
            List<ConfigFileTemplate> emptyList = List.of();
            List<ToolType> toolTypes = List.of(ToolType.CLAUDE);

            given(configFileTemplateReadManager.findForMcp(TechStackId.of(1L), 1L, toolTypes))
                    .willReturn(emptyList);
            given(configFileResultAssembler.toConfigFilesResult(emptyList))
                    .willReturn(configFilesResult);

            // when
            ConfigFilesResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(configFilesResult);

            then(configFileTemplateReadManager)
                    .should()
                    .findForMcp(TechStackId.of(1L), 1L, toolTypes);
            then(configFileResultAssembler).should().toConfigFilesResult(emptyList);
        }
    }
}
