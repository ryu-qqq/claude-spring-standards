package com.ryuqq.adapter.in.rest.zerotolerance.controller.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.SearchZeroToleranceRulesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.response.ZeroToleranceRuleDetailApiResponseFixture;
import com.ryuqq.adapter.in.rest.zerotolerance.ZeroToleranceRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.zerotolerance.mapper.ZeroToleranceRuleQueryApiMapper;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.port.in.SearchZeroToleranceRulesByCursorUseCase;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ZeroToleranceRuleQueryController REST Docs 테스트
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
@WebMvcTest(ZeroToleranceRuleQueryController.class)
@DisplayName("ZeroToleranceRuleQueryController REST Docs")
class ZeroToleranceRuleQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private SearchZeroToleranceRulesByCursorUseCase searchZeroToleranceRulesByCursorUseCase;

    @MockitoBean private ZeroToleranceRuleQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/zero-tolerance-rules - ZeroToleranceRule 복합 조건 조회")
    class SearchZeroToleranceRulesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var request = SearchZeroToleranceRulesCursorApiRequestFixture.valid();
            var searchParams =
                    ZeroToleranceRuleSearchParams.of(
                            CommonCursorParams.of(request.cursor(), request.size()),
                            request.conventionIds(),
                            request.detectionTypes(),
                            request.searchField(),
                            request.searchWord(),
                            request.autoRejectPr());
            var codingRuleResult1 =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Lombok 사용 금지",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Domain 레이어에서 Lombok 어노테이션 사용을 금지합니다.",
                            "Lombok은 바이트코드 조작으로 예측 불가능한 동작을 유발할 수 있습니다.",
                            false,
                            List.of("AGGREGATE", "ENTITY", "VALUE_OBJECT"),
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var exampleResult1 =
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
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var checklistItemResult1 =
                    new ChecklistItemResult(
                            1L,
                            1L,
                            1,
                            "Lombok 어노테이션 사용 여부 확인",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "AGG-001-CHECK-1",
                            true,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var detailResult1 =
                    new ZeroToleranceRuleDetailResult(
                            codingRuleResult1,
                            List.of(exampleResult1),
                            List.of(checklistItemResult1));
            var response1 = ZeroToleranceRuleDetailApiResponseFixture.valid();

            var sliceResult = new ZeroToleranceRuleSliceResult(List.of(detailResult1), true, 2L);
            var sliceResponse =
                    new com.ryuqq.adapter.in.rest.zerotolerance.dto.response
                            .ZeroToleranceRuleSliceApiResponse(List.of(response1), true, 2L);

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchZeroToleranceRulesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceApiResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(ZeroToleranceRuleApiEndpoints.BASE)
                                    .param("conventionIds", "1", "2")
                                    .param("detectionTypes", "REGEX", "AST")
                                    .param("searchField", "TYPE")
                                    .param("searchWord", "LOMBOK_IN_DOMAIN")
                                    .param("autoRejectPr", "true")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.rules").isArray())
                    .andExpect(jsonPath("$.data.rules[0].id").value(1L))
                    .andExpect(jsonPath("$.data.hasNext").value(true))
                    .andDo(
                            document(
                                    "zero-tolerance-rule-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("슬라이스 크기 (1~100)")
                                                    .optional(),
                                            parameterWithName("conventionIds")
                                                    .description("컨벤션 ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("detectionTypes")
                                                    .description("탐지 방식 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (TYPE)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("autoRejectPr")
                                                    .description("PR 자동 거부 여부 필터")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.rules")
                                                    .description("Zero-Tolerance 규칙 상세 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.rules[].id")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.rules[].code")
                                                    .description("규칙 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].name")
                                                    .description("규칙 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].severity")
                                                    .description("심각도")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].category")
                                                    .description("카테고리")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].description")
                                                    .description("규칙 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].rationale")
                                                    .description("규칙 근거")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].autoFixable")
                                                    .description("자동 수정 가능 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.rules[].appliesTo")
                                                    .description("적용 대상 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.rules[].examples")
                                                    .description("규칙 예시 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.rules[].examples[].ruleExampleId")
                                                    .description("규칙 예시 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.rules[].examples[].ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.rules[].examples[].exampleType")
                                                    .description("예시 타입 (GOOD, BAD)")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].examples[].code")
                                                    .description("예시 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].examples[].language")
                                                    .description("언어")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].examples[].explanation")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.rules[].examples[].highlightLines")
                                                    .description("하이라이트 라인 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.rules[].examples[].source")
                                                    .description("예시 소스 (MANUAL, AGENT_FEEDBACK)")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].examples[].feedbackId")
                                                    .description("피드백 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.rules[].examples[].createdAt")
                                                    .description("생성 일시 (ISO 8601 형식)")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].examples[].updatedAt")
                                                    .description("수정 일시 (ISO 8601 형식)")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].checklistItems")
                                                    .description("체크리스트 항목 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.rules[].checklistItems[].id")
                                                    .description("체크리스트 항목 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.rules[].checklistItems[].ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath(
                                                            "data.rules[].checklistItems[].sequenceOrder")
                                                    .description("순서")
                                                    .type(Integer.class),
                                            fieldWithPath(
                                                            "data.rules[].checklistItems[].checkDescription")
                                                    .description("체크 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].checklistItems[].checkType")
                                                    .description(
                                                            "체크 타입 (AUTOMATED, MANUAL, SEMI_AUTO)")
                                                    .type(String.class),
                                            fieldWithPath(
                                                            "data.rules[].checklistItems[].automationTool")
                                                    .description("자동화 도구")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.rules[].checklistItems[].automationRuleId")
                                                    .description("자동화 규칙 ID")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.rules[].checklistItems[].critical")
                                                    .description("필수 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.rules[].checklistItems[].source")
                                                    .description(
                                                            "체크리스트 소스 (MANUAL, AGENT_FEEDBACK)")
                                                    .type(String.class),
                                            fieldWithPath(
                                                            "data.rules[].checklistItems[].feedbackId")
                                                    .description("피드백 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.rules[].checklistItems[].createdAt")
                                                    .description("생성 일시 (ISO 8601 형식)")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].checklistItems[].updatedAt")
                                                    .description("수정 일시 (ISO 8601 형식)")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.rules[].updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.hasNext")
                                                    .description("다음 페이지 존재 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.nextCursorId")
                                                    .description("다음 페이지 커서 ID")
                                                    .type(Long.class)
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
            mockMvc.perform(get(ZeroToleranceRuleApiEndpoints.BASE).param("size", "101"))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "zero-tolerance-rule-search-size-validation-error",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("슬라이스 크기 (1~100)")
                                                    .optional(),
                                            parameterWithName("conventionIds")
                                                    .description("컨벤션 ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("detectionTypes")
                                                    .description("탐지 방식 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (TYPE)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("autoRejectPr")
                                                    .description("PR 자동 거부 여부 필터")
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
