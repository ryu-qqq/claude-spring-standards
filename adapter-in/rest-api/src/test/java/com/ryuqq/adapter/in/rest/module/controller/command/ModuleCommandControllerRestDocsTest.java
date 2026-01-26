package com.ryuqq.adapter.in.rest.module.controller.command;

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
import com.ryuqq.adapter.in.rest.fixture.request.CreateModuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateModuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.module.ModuleApiEndpoints;
import com.ryuqq.adapter.in.rest.module.mapper.ModuleCommandApiMapper;
import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import com.ryuqq.application.module.port.in.CreateModuleUseCase;
import com.ryuqq.application.module.port.in.UpdateModuleUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ModuleCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ModuleCommandController.class)
@DisplayName("ModuleCommandController REST Docs")
class ModuleCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateModuleUseCase createModuleUseCase;

    @MockitoBean private UpdateModuleUseCase updateModuleUseCase;

    @MockitoBean private ModuleCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/modules - Module 생성")
    class CreateModule {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateModuleApiRequestFixture.valid();
            var command =
                    new CreateModuleCommand(
                            1L,
                            null,
                            "adapter-in-rest-api",
                            "REST API Adapter",
                            "adapter-in/rest-api",
                            ":adapter-in:rest-api");
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createModuleUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ModuleApiEndpoints.MODULES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.moduleId").value(createdId))
                    .andDo(
                            document(
                                    "module-create",
                                    requestFields(
                                            fieldWithPath("layerId")
                                                    .description("레이어 ID")
                                                    .type(Long.class),
                                            fieldWithPath("parentModuleId")
                                                    .description("부모 모듈 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("name")
                                                    .description("모듈 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("modulePath")
                                                    .description("모듈 경로")
                                                    .type(String.class),
                                            fieldWithPath("buildIdentifier")
                                                    .description("빌드 식별자")
                                                    .type(String.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.moduleId")
                                                    .description("생성된 Module ID")
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
            var request = CreateModuleApiRequestFixture.invalidWithBlankName();

            // When & Then
            mockMvc.perform(
                            post(ModuleApiEndpoints.MODULES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("module-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/modules/{moduleId} - Module 수정")
    class UpdateModule {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long moduleId = 1L;
            var request = UpdateModuleApiRequestFixture.valid();
            var command =
                    new UpdateModuleCommand(
                            1L,
                            null,
                            "adapter-in-rest-api",
                            "REST API Adapter",
                            "adapter-in/rest-api",
                            ":adapter-in:rest-api");

            given(mapper.toCommand(eq(moduleId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ModuleApiEndpoints.MODULES + "/{moduleId}", moduleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "module-update",
                                    pathParameters(
                                            parameterWithName("moduleId").description("Module ID")),
                                    requestFields(
                                            fieldWithPath("parentModuleId")
                                                    .description("부모 모듈 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("name")
                                                    .description("모듈 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("modulePath")
                                                    .description("모듈 경로")
                                                    .type(String.class),
                                            fieldWithPath("buildIdentifier")
                                                    .description("빌드 식별자")
                                                    .type(String.class)),
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
