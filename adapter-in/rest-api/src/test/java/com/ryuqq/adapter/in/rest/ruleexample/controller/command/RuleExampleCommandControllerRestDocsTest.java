package com.ryuqq.adapter.in.rest.ruleexample.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateRuleExampleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateRuleExampleApiRequestFixture;
import com.ryuqq.adapter.in.rest.ruleexample.RuleExampleApiEndpoints;
import com.ryuqq.adapter.in.rest.ruleexample.mapper.RuleExampleCommandApiMapper;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.port.in.CreateRuleExampleUseCase;
import com.ryuqq.application.ruleexample.port.in.UpdateRuleExampleUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * RuleExampleCommandController REST Docs 테스트
 *
 * <p>REST Docs 문서화를 위한 통합 테스트입니다.
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API 요청/응답 필드 문서화
 *   <li>Path Parameter 문서화
 *   <li>정상/예외 응답 시나리오 문서화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(RuleExampleCommandController.class)
@DisplayName("RuleExampleCommandController REST Docs")
class RuleExampleCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateRuleExampleUseCase createRuleExampleUseCase;

    @MockitoBean private UpdateRuleExampleUseCase updateRuleExampleUseCase;

    @MockitoBean private RuleExampleCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/rule-examples - RuleExample 생성")
    class CreateRuleExample {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateRuleExampleApiRequestFixture.valid();
            var command =
                    new CreateRuleExampleCommand(
                            1L, "GOOD", "code", "JAVA", "explanation", java.util.List.of(1, 2));
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createRuleExampleUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(RuleExampleApiEndpoints.RULE_EXAMPLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.ruleExampleId").value(createdId))
                    .andDo(
                            document(
                                    "rule-example-create",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("exampleType")
                                                    .description("예시 타입 (GOOD, BAD)")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("예시 코드")
                                                    .type(String.class),
                                            fieldWithPath("language")
                                                    .description("언어 (예: JAVA, KOTLIN)")
                                                    .type(String.class),
                                            fieldWithPath("explanation")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("highlightLines")
                                                    .description("하이라이트 라인 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.ruleExampleId")
                                                    .description("생성된 RuleExample ID")
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
            var request = CreateRuleExampleApiRequestFixture.invalidWithBlankCode();

            // When & Then
            mockMvc.perform(
                            post(RuleExampleApiEndpoints.RULE_EXAMPLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "rule-example-create-validation-error",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("exampleType")
                                                    .description("예시 타입 (GOOD, BAD)")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("예시 코드 (필수)")
                                                    .type(String.class),
                                            fieldWithPath("language")
                                                    .description("언어")
                                                    .type(String.class),
                                            fieldWithPath("explanation")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("highlightLines")
                                                    .description("하이라이트 라인 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("code 길이 초과 시 400 Bad Request 반환")
        void codeTooLong_ShouldReturn400() throws Exception {
            // Given
            var request = CreateRuleExampleApiRequestFixture.invalidWithLongCode();

            // When & Then
            mockMvc.perform(
                            post(RuleExampleApiEndpoints.RULE_EXAMPLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "rule-example-create-code-length-error",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("exampleType")
                                                    .description("예시 타입")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("예시 코드 (최대 10000자)")
                                                    .type(String.class),
                                            fieldWithPath("language")
                                                    .description("언어")
                                                    .type(String.class),
                                            fieldWithPath("explanation")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("highlightLines")
                                                    .description("하이라이트 라인 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/rule-examples/{ruleExampleId} - RuleExample 수정")
    class UpdateRuleExample {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long ruleExampleId = 1L;
            var request = UpdateRuleExampleApiRequestFixture.valid();
            var command =
                    new UpdateRuleExampleCommand(
                            1L, "GOOD", "code", "JAVA", "explanation", java.util.List.of(1, 2));

            given(mapper.toCommand(eq(ruleExampleId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(RuleExampleApiEndpoints.BY_ID, ruleExampleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "rule-example-update",
                                    pathParameters(
                                            parameterWithName("ruleExampleId")
                                                    .description("RuleExample ID")),
                                    requestFields(
                                            fieldWithPath("exampleType")
                                                    .description("예시 타입 (GOOD, BAD)")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("예시 코드")
                                                    .type(String.class),
                                            fieldWithPath("language")
                                                    .description("언어")
                                                    .type(String.class),
                                            fieldWithPath("explanation")
                                                    .description("설명 (필수)")
                                                    .type(String.class),
                                            fieldWithPath("highlightLines")
                                                    .description("하이라이트 라인 목록 (필수)")
                                                    .type(java.util.List.class)),
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
            Long ruleExampleId = 1L;
            var request = UpdateRuleExampleApiRequestFixture.invalidWithBlankCode();

            // When & Then
            mockMvc.perform(
                            put(RuleExampleApiEndpoints.BY_ID, ruleExampleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "rule-example-update-validation-error",
                                    pathParameters(
                                            parameterWithName("ruleExampleId")
                                                    .description("RuleExample ID")),
                                    requestFields(
                                            fieldWithPath("exampleType")
                                                    .description("예시 타입")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("예시 코드 (필수)")
                                                    .type(String.class),
                                            fieldWithPath("language")
                                                    .description("언어")
                                                    .type(String.class),
                                            fieldWithPath("explanation")
                                                    .description("설명")
                                                    .type(String.class),
                                            fieldWithPath("highlightLines")
                                                    .description("하이라이트 라인 목록")
                                                    .type(java.util.List.class)),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }
    }
}
