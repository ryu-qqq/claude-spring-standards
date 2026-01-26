package com.ryuqq.adapter.in.rest.codingrule.controller.command;

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

import com.ryuqq.adapter.in.rest.codingrule.CodingRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.codingrule.mapper.CodingRuleCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateCodingRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateCodingRuleApiRequestFixture;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.port.in.CreateCodingRuleUseCase;
import com.ryuqq.application.codingrule.port.in.UpdateCodingRuleUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * CodingRuleCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(CodingRuleCommandController.class)
@DisplayName("CodingRuleCommandController REST Docs")
class CodingRuleCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateCodingRuleUseCase createCodingRuleUseCase;

    @MockitoBean private UpdateCodingRuleUseCase updateCodingRuleUseCase;

    @MockitoBean private CodingRuleCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/coding-rules - CodingRule 생성")
    class CreateCodingRule {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateCodingRuleApiRequestFixture.valid();
            var command =
                    new CreateCodingRuleCommand(
                            1L,
                            null,
                            "AGG-001",
                            "Lombok 사용 금지",
                            "BLOCKER",
                            "ANNOTATION",
                            "Description",
                            null,
                            false,
                            java.util.List.of("AGGREGATE", "VALUE_OBJECT"),
                            null,
                            null,
                            null);
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createCodingRuleUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(CodingRuleApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.codingRuleId").value(createdId))
                    .andDo(
                            document(
                                    "coding-rule-create",
                                    requestFields(
                                            fieldWithPath("conventionId")
                                                    .description("컨벤션 ID")
                                                    .type(Long.class),
                                            fieldWithPath("structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("code")
                                                    .description("규칙 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("규칙 이름")
                                                    .type(String.class),
                                            fieldWithPath("severity")
                                                    .description("심각도")
                                                    .type(String.class),
                                            fieldWithPath("category")
                                                    .description("카테고리")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("규칙 설명")
                                                    .type(String.class),
                                            fieldWithPath("rationale")
                                                    .description("규칙 근거")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("autoFixable")
                                                    .description("자동 수정 가능 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("appliesTo")
                                                    .description("적용 대상 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("sdkConstraint")
                                                    .description("SDK 제약 조건")
                                                    .type(Object.class)
                                                    .optional(),
                                            fieldWithPath("sdkConstraint.artifact")
                                                    .description("SDK 아티팩트")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("sdkConstraint.minVersion")
                                                    .description("최소 버전")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("sdkConstraint.maxVersion")
                                                    .description("최대 버전")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.codingRuleId")
                                                    .description("생성된 CodingRule ID")
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
            var request = CreateCodingRuleApiRequestFixture.invalidWithBlankCode();

            // When & Then
            mockMvc.perform(
                            post(CodingRuleApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("coding-rule-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/coding-rules/{codingRuleId} - CodingRule 수정")
    class UpdateCodingRule {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long codingRuleId = 1L;
            var request = UpdateCodingRuleApiRequestFixture.valid();
            var command =
                    new UpdateCodingRuleCommand(
                            1L,
                            null,
                            "AGG-001",
                            "Lombok 사용 금지",
                            "BLOCKER",
                            "ANNOTATION",
                            "Description",
                            null,
                            false,
                            java.util.List.of("AGGREGATE", "VALUE_OBJECT"),
                            null,
                            null,
                            null);

            given(mapper.toCommand(eq(codingRuleId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(CodingRuleApiEndpoints.BY_ID, codingRuleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "coding-rule-update",
                                    pathParameters(
                                            parameterWithName("codingRuleId")
                                                    .description("CodingRule ID")),
                                    requestFields(
                                            fieldWithPath("structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("code")
                                                    .description("규칙 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("규칙 이름")
                                                    .type(String.class),
                                            fieldWithPath("severity")
                                                    .description("심각도")
                                                    .type(String.class),
                                            fieldWithPath("category")
                                                    .description("카테고리")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("규칙 설명")
                                                    .type(String.class),
                                            fieldWithPath("rationale")
                                                    .description("규칙 근거")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("autoFixable")
                                                    .description("자동 수정 가능 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("appliesTo")
                                                    .description("적용 대상 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("sdkConstraint")
                                                    .description("SDK 제약 조건")
                                                    .type(Object.class)
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
