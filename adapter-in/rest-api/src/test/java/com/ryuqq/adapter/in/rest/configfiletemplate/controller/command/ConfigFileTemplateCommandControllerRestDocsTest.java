package com.ryuqq.adapter.in.rest.configfiletemplate.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.configfiletemplate.ConfigFileTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.configfiletemplate.mapper.ConfigFileTemplateCommandApiMapper;
import com.ryuqq.adapter.in.rest.fixture.request.CreateConfigFileTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateConfigFileTemplateApiRequestFixture;
import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.dto.command.UpdateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.port.in.CreateConfigFileTemplateUseCase;
import com.ryuqq.application.configfiletemplate.port.in.UpdateConfigFileTemplateUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ConfigFileTemplateCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ConfigFileTemplateCommandController.class)
@DisplayName("ConfigFileTemplateCommandController REST Docs")
class ConfigFileTemplateCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateConfigFileTemplateUseCase createConfigFileTemplateUseCase;

    @MockitoBean private UpdateConfigFileTemplateUseCase updateConfigFileTemplateUseCase;

    @MockitoBean private ConfigFileTemplateCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/templates/config-files - ConfigFileTemplate 생성")
    class CreateConfigFileTemplate {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateConfigFileTemplateApiRequestFixture.valid();
            var command =
                    new CreateConfigFileTemplateCommand(
                            1L,
                            1L,
                            "CLAUDE",
                            ".claude/CLAUDE.md",
                            "CLAUDE.md",
                            "# Project Configuration",
                            "MAIN_CONFIG",
                            "Claude Code 메인 설정 파일",
                            "{\"project_name\": \"string\"}",
                            0,
                            true);
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createConfigFileTemplateUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ConfigFileTemplateApiEndpoints.CONFIG_FILES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "config-file-template-create",
                                    requestFields(
                                            fieldWithPath("techStackId")
                                                    .description("기술 스택 ID")
                                                    .type(Long.class),
                                            fieldWithPath("architectureId")
                                                    .description("아키텍처 ID (nullable)")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("toolType")
                                                    .description(
                                                            "도구 타입 (CLAUDE, CURSOR, COPILOT 등)")
                                                    .type(String.class),
                                            fieldWithPath("filePath")
                                                    .description("파일 경로 (예: .claude/CLAUDE.md)")
                                                    .type(String.class),
                                            fieldWithPath("fileName")
                                                    .description("파일명 (예: CLAUDE.md)")
                                                    .type(String.class),
                                            fieldWithPath("content")
                                                    .description("파일 내용")
                                                    .type(String.class),
                                            fieldWithPath("category")
                                                    .description(
                                                            "카테고리 (MAIN_CONFIG, SKILL, RULE, AGENT,"
                                                                    + " HOOK)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .description("템플릿 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("variables")
                                                    .description("치환 가능한 변수 정의 (JSON 문자열)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("displayOrder")
                                                    .description("정렬 순서")
                                                    .type(Integer.class)
                                                    .optional(),
                                            fieldWithPath("isRequired")
                                                    .description("필수 파일 여부")
                                                    .type(Boolean.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 ConfigFileTemplate ID")
                                                    .type(Long.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() throws Exception {
            // Given
            var request = CreateConfigFileTemplateApiRequestFixture.invalidWithBlankToolType();

            // When & Then
            mockMvc.perform(
                            post(ConfigFileTemplateApiEndpoints.CONFIG_FILES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("config-file-template-create-validation-error"));
        }
    }

    @Nested
    @DisplayName(
            "PUT /api/v1/templates/config-files/{configFileTemplateId} - ConfigFileTemplate 수정")
    class UpdateConfigFileTemplate {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long configFileTemplateId = 1L;
            var request = UpdateConfigFileTemplateApiRequestFixture.valid();
            var command =
                    new UpdateConfigFileTemplateCommand(
                            1L,
                            "CLAUDE",
                            ".claude/CLAUDE.md",
                            "CLAUDE.md",
                            "# Updated Configuration",
                            "MAIN_CONFIG",
                            "수정된 설명",
                            "{\"project_name\": \"string\"}",
                            0,
                            true);

            given(mapper.toCommand(eq(configFileTemplateId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(
                                            ConfigFileTemplateApiEndpoints.CONFIG_FILES
                                                    + ConfigFileTemplateApiEndpoints.ID,
                                            configFileTemplateId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "config-file-template-update",
                                    pathParameters(
                                            parameterWithName("configFileTemplateId")
                                                    .description("ConfigFileTemplate ID")),
                                    requestFields(
                                            fieldWithPath("toolType")
                                                    .description(
                                                            "도구 타입 (CLAUDE, CURSOR, COPILOT 등)")
                                                    .type(String.class),
                                            fieldWithPath("filePath")
                                                    .description("파일 경로")
                                                    .type(String.class),
                                            fieldWithPath("fileName")
                                                    .description("파일명")
                                                    .type(String.class),
                                            fieldWithPath("content")
                                                    .description("파일 내용")
                                                    .type(String.class),
                                            fieldWithPath("category")
                                                    .description("카테고리")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .description("템플릿 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("variables")
                                                    .description("치환 가능한 변수 정의 (JSON)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("displayOrder")
                                                    .description("정렬 순서")
                                                    .type(Integer.class)
                                                    .optional(),
                                            fieldWithPath("isRequired")
                                                    .description("필수 파일 여부")
                                                    .type(Boolean.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class)
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }
}
