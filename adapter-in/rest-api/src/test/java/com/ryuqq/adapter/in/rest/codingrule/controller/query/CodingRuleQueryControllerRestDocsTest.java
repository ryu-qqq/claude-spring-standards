package com.ryuqq.adapter.in.rest.codingrule.controller.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.codingrule.CodingRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.codingrule.mapper.CodingRuleQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.response.CodingRuleApiResponseFixture;
import com.ryuqq.adapter.in.rest.fixture.response.CodingRuleIndexApiResponseFixture;
import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.application.codingrule.port.in.ListCodingRuleIndexUseCase;
import com.ryuqq.application.codingrule.port.in.SearchCodingRulesByCursorUseCase;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * CodingRuleQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(CodingRuleQueryController.class)
@DisplayName("CodingRuleQueryController REST Docs")
class CodingRuleQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchCodingRulesByCursorUseCase searchCodingRulesByCursorUseCase;

    @MockitoBean private ListCodingRuleIndexUseCase listCodingRuleIndexUseCase;

    @MockitoBean private CodingRuleQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/coding-rules - CodingRule 복합 조건 조회")
    class SearchCodingRulesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    CodingRuleSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            null,
                            null,
                            null,
                            null);
            var result1 =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Lombok 사용 금지",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            null,
                            false,
                            List.of("AGGREGATE"),
                            null,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = CodingRuleApiResponseFixture.valid();

            var sliceResult = new CodingRuleSliceResult(List.of(result1), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchCodingRulesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(CodingRuleApiEndpoints.BASE).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "coding-rule-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("categories")
                                                    .description("카테고리 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("severities")
                                                    .description("심각도 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME, DESCRIPTION)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어 (부분 일치)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("CodingRule 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].codingRuleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].conventionId")
                                                    .description("컨벤션 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].code")
                                                    .description("규칙 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].name")
                                                    .description("규칙 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].severity")
                                                    .description("심각도")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].category")
                                                    .description("카테고리")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("규칙 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].rationale")
                                                    .description("규칙 근거")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].autoFixable")
                                                    .description("자동 수정 가능 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.content[].appliesTo")
                                                    .description("적용 대상 목록")
                                                    .type(java.util.List.class),
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
                                                    .description("다음 커서")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/templates/coding-rules/index - CodingRule 인덱스 조회")
    class ListCodingRuleIndex {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams = CodingRuleIndexSearchParams.all();
            var item1 = CodingRuleIndexItem.of("AGG-001", "Lombok 사용 금지", "BLOCKER", "ANNOTATION");
            var item2 = CodingRuleIndexItem.of("AGG-002", "Getter 체이닝 금지", "BLOCKER", "BEHAVIOR");
            var items = List.of(item1, item2);
            var responses = CodingRuleIndexApiResponseFixture.validList();

            given(mapper.toIndexSearchParams(any())).willReturn(searchParams);
            given(listCodingRuleIndexUseCase.execute(any())).willReturn(items);
            given(mapper.toIndexResponses(any())).willReturn(responses);

            // When & Then
            mockMvc.perform(get(CodingRuleApiEndpoints.BASE + "/index"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andDo(
                            document(
                                    "coding-rule-index",
                                    queryParameters(
                                            parameterWithName("conventionId")
                                                    .description("컨벤션 ID (null이면 전체)")
                                                    .optional(),
                                            parameterWithName("severities")
                                                    .description(
                                                            "심각도 필터 목록 (BLOCKER, CRITICAL, MAJOR,"
                                                                    + " MINOR)")
                                                    .optional(),
                                            parameterWithName("categories")
                                                    .description(
                                                            "카테고리 필터 목록 (STRUCTURE, NAMING,"
                                                                    + " DEPENDENCY 등)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("규칙 인덱스 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data[].code")
                                                    .description("규칙 코드 (예: DOM-AGG-001)")
                                                    .type(String.class),
                                            fieldWithPath("data[].name")
                                                    .description("규칙 이름")
                                                    .type(String.class),
                                            fieldWithPath("data[].severity")
                                                    .description(
                                                            "심각도 (BLOCKER, CRITICAL, MAJOR, MINOR)")
                                                    .type(String.class),
                                            fieldWithPath("data[].category")
                                                    .description(
                                                            "카테고리 (STRUCTURE, NAMING, DEPENDENCY"
                                                                    + " 등)")
                                                    .type(String.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필터 요청 시 200 OK 반환")
        void withFilters_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    CodingRuleIndexSearchParams.of(1L, List.of("BLOCKER"), List.of("ANNOTATION"));
            var item1 = CodingRuleIndexItem.of("AGG-001", "Lombok 사용 금지", "BLOCKER", "ANNOTATION");
            var items = List.of(item1);
            var responses = List.of(CodingRuleIndexApiResponseFixture.withCode("AGG-001"));

            given(mapper.toIndexSearchParams(any())).willReturn(searchParams);
            given(listCodingRuleIndexUseCase.execute(any())).willReturn(items);
            given(mapper.toIndexResponses(any())).willReturn(responses);

            // When & Then
            mockMvc.perform(
                            get(CodingRuleApiEndpoints.BASE + "/index")
                                    .param("conventionId", "1")
                                    .param("severities", "BLOCKER")
                                    .param("categories", "ANNOTATION"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].code").value("AGG-001"));
        }
    }
}
