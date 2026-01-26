package com.ryuqq.adapter.in.rest.ruleexample.controller.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.response.RuleExampleApiResponseFixture;
import com.ryuqq.adapter.in.rest.ruleexample.RuleExampleApiEndpoints;
import com.ryuqq.adapter.in.rest.ruleexample.mapper.RuleExampleQueryApiMapper;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.application.ruleexample.port.in.SearchRuleExamplesByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * RuleExampleQueryController REST Docs 테스트
 *
 * <p>REST Docs 문서화를 위한 통합 테스트입니다.
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API 요청/응답 필드 문서화
 *   <li>Path Parameter 문서화
 *   <li>Query Parameter 문서화
 *   <li>정상/예외 응답 시나리오 문서화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(RuleExampleQueryController.class)
@DisplayName("RuleExampleQueryController REST Docs")
class RuleExampleQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchRuleExamplesByCursorUseCase searchRuleExamplesByCursorUseCase;

    @MockitoBean private RuleExampleQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/rule-examples - RuleExample 복합 조건 조회")
    class SearchRuleExamplesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    RuleExampleSearchParams.of(
                            CommonCursorParams.of(null, 20),
                            List.of(1L),
                            List.of("GOOD"),
                            List.of("JAVA"));
            var result1 =
                    new RuleExampleResult(
                            1L,
                            1L,
                            "GOOD",
                            "public class Order {\n    private final OrderId id;\n}",
                            "JAVA",
                            "Aggregate 클래스 예시",
                            List.of(1, 2),
                            "MANUAL",
                            null,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var result2 =
                    new RuleExampleResult(
                            2L,
                            1L,
                            "BAD",
                            "@Data\npublic class Order {\n    private OrderId id;\n}",
                            "JAVA",
                            "Lombok 사용 금지 예시",
                            List.of(1),
                            "MANUAL",
                            null,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = RuleExampleApiResponseFixture.valid();
            var response2 = RuleExampleApiResponseFixture.badExample();

            var sliceResult = new RuleExampleSliceResult(List.of(result1, result2), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1, response2), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchRuleExamplesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(RuleExampleApiEndpoints.RULE_EXAMPLES)
                                    .param("ruleIds", "1")
                                    .param("exampleTypes", "GOOD")
                                    .param("languages", "JAVA")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].ruleExampleId").value(1L))
                    .andExpect(jsonPath("$.data.hasNext").value(true))
                    .andDo(
                            document(
                                    "rule-example-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20, 최대: 100)"),
                                            parameterWithName("ruleIds")
                                                    .description("코딩 규칙 ID 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("exampleTypes")
                                                    .description("예시 타입 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("languages")
                                                    .description("언어 필터 (복수)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("RuleExample 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].ruleExampleId")
                                                    .description("규칙 예시 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].exampleType")
                                                    .description("예시 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].code")
                                                    .description("예시 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].language")
                                                    .description("언어")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].explanation")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].highlightLines")
                                                    .description("하이라이트 라인 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].source")
                                                    .description("예시 소스")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].feedbackId")
                                                    .description("피드백 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.size")
                                                    .description("슬라이스 크기")
                                                    .type(Integer.class),
                                            fieldWithPath("data.hasNext")
                                                    .description("다음 슬라이스 존재 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.nextCursor")
                                                    .description("다음 슬라이스 조회를 위한 커서")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("size가 범위를 벗어나면 400 Bad Request 반환")
        void sizeOutOfRange_ShouldReturn400() throws Exception {
            // When & Then
            mockMvc.perform(get(RuleExampleApiEndpoints.RULE_EXAMPLES).param("size", "101"))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "rule-example-search-by-cursor-size-validation-error",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("ruleIds")
                                                    .description("코딩 규칙 ID 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("exampleTypes")
                                                    .description("예시 타입 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("languages")
                                                    .description("언어 필터 (복수)")
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
}
