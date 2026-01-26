package com.ryuqq.adapter.in.rest.onboardingcontext.controller.command;

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
import com.ryuqq.adapter.in.rest.fixture.request.CreateOnboardingContextApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateOnboardingContextApiRequestFixture;
import com.ryuqq.adapter.in.rest.onboardingcontext.OnboardingContextApiEndpoints;
import com.ryuqq.adapter.in.rest.onboardingcontext.mapper.OnboardingContextCommandApiMapper;
import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.port.in.CreateOnboardingContextUseCase;
import com.ryuqq.application.onboardingcontext.port.in.UpdateOnboardingContextUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * OnboardingContextCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(OnboardingContextCommandController.class)
@DisplayName("OnboardingContextCommandController REST Docs")
class OnboardingContextCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateOnboardingContextUseCase createOnboardingContextUseCase;

    @MockitoBean private UpdateOnboardingContextUseCase updateOnboardingContextUseCase;

    @MockitoBean private OnboardingContextCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/templates/onboarding-contexts - OnboardingContext 생성")
    class CreateOnboardingContext {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateOnboardingContextApiRequestFixture.valid();
            var command =
                    new CreateOnboardingContextCommand(
                            1L,
                            1L,
                            "SUMMARY",
                            "프로젝트 개요",
                            "# 프로젝트 개요\n\n이 프로젝트는 Spring Boot 기반입니다.",
                            0);
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createOnboardingContextUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(OnboardingContextApiEndpoints.ONBOARDING_CONTEXTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "onboarding-context-create",
                                    requestFields(
                                            fieldWithPath("techStackId")
                                                    .description("기술 스택 ID")
                                                    .type(Long.class),
                                            fieldWithPath("architectureId")
                                                    .description("아키텍처 ID (nullable)")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("contextType")
                                                    .description(
                                                            "컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE,"
                                                                    + " RULES_INDEX, MCP_USAGE)")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("컨텍스트 제목")
                                                    .type(String.class),
                                            fieldWithPath("content")
                                                    .description("컨텍스트 내용 (Markdown 지원)")
                                                    .type(String.class),
                                            fieldWithPath("priority")
                                                    .description("온보딩 시 표시 순서 (낮을수록 먼저)")
                                                    .type(Integer.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 OnboardingContext ID")
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
            var request = CreateOnboardingContextApiRequestFixture.invalidWithBlankContextType();

            // When & Then
            mockMvc.perform(
                            post(OnboardingContextApiEndpoints.ONBOARDING_CONTEXTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("onboarding-context-create-validation-error"));
        }
    }

    @Nested
    @DisplayName(
            "PUT /api/v1/templates/onboarding-contexts/{onboardingContextId} - OnboardingContext"
                    + " 수정")
    class UpdateOnboardingContext {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long onboardingContextId = 1L;
            var request = UpdateOnboardingContextApiRequestFixture.valid();
            var command =
                    new UpdateOnboardingContextCommand(
                            1L, "SUMMARY", "프로젝트 개요 (수정됨)", "# 수정된 내용", 0);

            given(mapper.toCommand(eq(onboardingContextId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(
                                            OnboardingContextApiEndpoints.ONBOARDING_CONTEXTS
                                                    + OnboardingContextApiEndpoints.ID,
                                            onboardingContextId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "onboarding-context-update",
                                    pathParameters(
                                            parameterWithName("onboardingContextId")
                                                    .description("OnboardingContext ID")),
                                    requestFields(
                                            fieldWithPath("contextType")
                                                    .description("컨텍스트 타입")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("컨텍스트 제목")
                                                    .type(String.class),
                                            fieldWithPath("content")
                                                    .description("컨텍스트 내용")
                                                    .type(String.class),
                                            fieldWithPath("priority")
                                                    .description("온보딩 시 표시 순서")
                                                    .type(Integer.class)
                                                    .optional()),
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
