package com.ryuqq.adapter.in.rest.zerotolerance.controller.command;

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
import com.ryuqq.adapter.in.rest.fixture.request.CreateZeroToleranceRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateZeroToleranceRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.zerotolerance.ZeroToleranceRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.zerotolerance.mapper.ZeroToleranceRuleCommandApiMapper;
import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.port.in.CreateZeroToleranceRuleUseCase;
import com.ryuqq.application.zerotolerance.port.in.UpdateZeroToleranceRuleUseCase;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ZeroToleranceRuleCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ZeroToleranceRuleCommandController.class)
@DisplayName("ZeroToleranceRuleCommandController REST Docs")
class ZeroToleranceRuleCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateZeroToleranceRuleUseCase createZeroToleranceRuleUseCase;

    @MockitoBean private UpdateZeroToleranceRuleUseCase updateZeroToleranceRuleUseCase;

    @MockitoBean private ZeroToleranceRuleCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/standards/zero-tolerance-rules - Zero-Tolerance 규칙 생성")
    class CreateZeroToleranceRule {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateZeroToleranceRuleApiRequestFixture.valid();
            var command =
                    new CreateZeroToleranceRuleCommand(
                            1L,
                            "ARCHITECTURE",
                            "@(Data|Getter|Setter)",
                            DetectionType.REGEX,
                            true,
                            "Lombok 금지");
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createZeroToleranceRuleUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ZeroToleranceRuleApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.zeroToleranceRuleId").value(createdId))
                    .andDo(
                            document(
                                    "zero-tolerance-rule-create",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("CodingRule ID")
                                                    .type(Long.class),
                                            fieldWithPath("type")
                                                    .description(
                                                            "Zero-Tolerance 타입 (SECURITY,"
                                                                + " ARCHITECTURE, CODE_QUALITY)")
                                                    .type(String.class),
                                            fieldWithPath("detectionPattern")
                                                    .description("탐지 패턴 (정규식 또는 AST 패턴)")
                                                    .type(String.class),
                                            fieldWithPath("detectionType")
                                                    .description("탐지 방식 (REGEX, AST, ARCHUNIT)")
                                                    .type(String.class),
                                            fieldWithPath("autoRejectPr")
                                                    .description("PR 자동 거부 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("errorMessage")
                                                    .description("위반 시 표시할 에러 메시지")
                                                    .type(String.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.zeroToleranceRuleId")
                                                    .description("생성된 Zero-Tolerance 규칙 ID")
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
            var request = CreateZeroToleranceRuleApiRequestFixture.invalidWithBlankType();

            // When & Then
            mockMvc.perform(
                            post(ZeroToleranceRuleApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("zero-tolerance-rule-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/standards/zero-tolerance-rules/{id} - Zero-Tolerance 규칙 수정")
    class UpdateZeroToleranceRule {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long zeroToleranceRuleId = 1L;
            var request = UpdateZeroToleranceRuleApiRequestFixture.valid();
            var command =
                    new UpdateZeroToleranceRuleCommand(
                            1L,
                            "ARCHITECTURE",
                            "@(Data|Getter|Setter)",
                            DetectionType.REGEX,
                            true,
                            "Lombok 금지");

            given(mapper.toCommand(eq(zeroToleranceRuleId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ZeroToleranceRuleApiEndpoints.BY_ID, zeroToleranceRuleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "zero-tolerance-rule-update",
                                    pathParameters(
                                            parameterWithName("id")
                                                    .description("Zero-Tolerance 규칙 ID")),
                                    requestFields(
                                            fieldWithPath("type")
                                                    .description(
                                                            "Zero-Tolerance 타입 (SECURITY,"
                                                                + " ARCHITECTURE, CODE_QUALITY)")
                                                    .type(String.class),
                                            fieldWithPath("detectionPattern")
                                                    .description("탐지 패턴 (정규식 또는 AST 패턴)")
                                                    .type(String.class),
                                            fieldWithPath("detectionType")
                                                    .description("탐지 방식 (REGEX, AST, ARCHUNIT)")
                                                    .type(String.class),
                                            fieldWithPath("autoRejectPr")
                                                    .description("PR 자동 거부 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("errorMessage")
                                                    .description("위반 시 표시할 에러 메시지")
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

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() throws Exception {
            // Given
            Long zeroToleranceRuleId = 1L;
            var request = UpdateZeroToleranceRuleApiRequestFixture.invalidWithBlankType();

            // When & Then
            mockMvc.perform(
                            put(ZeroToleranceRuleApiEndpoints.BY_ID, zeroToleranceRuleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("zero-tolerance-rule-update-validation-error"));
        }
    }
}
