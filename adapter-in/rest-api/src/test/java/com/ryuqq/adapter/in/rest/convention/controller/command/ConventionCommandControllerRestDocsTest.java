package com.ryuqq.adapter.in.rest.convention.controller.command;

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
import com.ryuqq.adapter.in.rest.convention.ConventionApiEndpoints;
import com.ryuqq.adapter.in.rest.convention.mapper.ConventionCommandApiMapper;
import com.ryuqq.adapter.in.rest.fixture.request.CreateConventionApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateConventionApiRequestFixture;
import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import com.ryuqq.application.convention.port.in.CreateConventionUseCase;
import com.ryuqq.application.convention.port.in.UpdateConventionUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ConventionCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ConventionCommandController.class)
@DisplayName("ConventionCommandController REST Docs")
class ConventionCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateConventionUseCase createConventionUseCase;

    @MockitoBean private UpdateConventionUseCase updateConventionUseCase;

    @MockitoBean private ConventionCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/conventions - Convention 생성")
    class CreateConvention {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateConventionApiRequestFixture.valid();
            var command = new CreateConventionCommand(1L, "1.0.0", "Domain Layer Convention");
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createConventionUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ConventionApiEndpoints.CONVENTIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "convention-create",
                                    requestFields(
                                            fieldWithPath("moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("version")
                                                    .description("버전")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 Convention ID")
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
            var request = CreateConventionApiRequestFixture.invalidWithNullModuleId();

            // When & Then
            mockMvc.perform(
                            post(ConventionApiEndpoints.CONVENTIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("convention-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/conventions/{conventionId} - Convention 수정")
    class UpdateConvention {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long conventionId = 1L;
            var request = UpdateConventionApiRequestFixture.valid();
            var command =
                    new UpdateConventionCommand(1L, 1L, "1.0.0", "Domain Layer Convention", true);

            given(mapper.toCommand(eq(conventionId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ConventionApiEndpoints.CONVENTION_DETAIL, conventionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "convention-update",
                                    pathParameters(
                                            parameterWithName("conventionId")
                                                    .description("Convention ID")),
                                    requestFields(
                                            fieldWithPath("moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("version")
                                                    .description("버전")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class),
                                            fieldWithPath("active")
                                                    .description("활성화 여부")
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
