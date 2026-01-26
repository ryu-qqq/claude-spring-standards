package com.ryuqq.adapter.in.rest.layerdependency.controller.query;

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

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.SearchLayerDependencyRulesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.response.LayerDependencyRuleApiResponseFixture;
import com.ryuqq.adapter.in.rest.layerdependency.LayerDependencyRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.layerdependency.mapper.LayerDependencyRuleQueryApiMapper;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleSliceResult;
import com.ryuqq.application.layerdependency.port.in.SearchLayerDependencyRulesByCursorUseCase;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * LayerDependencyRuleQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(LayerDependencyRuleQueryController.class)
@DisplayName("LayerDependencyRuleQueryController REST Docs")
class LayerDependencyRuleQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private SearchLayerDependencyRulesByCursorUseCase searchLayerDependencyRulesByCursorUseCase;

    @MockitoBean private LayerDependencyRuleQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName(
            "GET /api/v1/templates/layer-dependency-rules -"
                    + " LayerDependencyRule 복합 조건 조회 (커서 기반)")
    class SearchLayerDependencyRulesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var request = SearchLayerDependencyRulesCursorApiRequestFixture.valid();
            var searchParams =
                    LayerDependencyRuleSearchParams.of(
                            CommonCursorParams.of(request.cursor(), request.size()),
                            request.architectureIds(),
                            request.dependencyTypes(),
                            request.searchField(),
                            request.searchWord());
            var result1 =
                    new LayerDependencyRuleResult(
                            1L,
                            1L,
                            LayerType.DOMAIN,
                            LayerType.APPLICATION,
                            DependencyType.ALLOWED,
                            null,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = LayerDependencyRuleApiResponseFixture.valid();

            var sliceResult = new LayerDependencyRuleSliceResult(List.of(result1), 20, true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchLayerDependencyRulesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(LayerDependencyRuleApiEndpoints.QUERY_BASE)
                                    .param("architectureIds", "1", "2")
                                    .param("dependencyTypes", "ALLOWED", "FORBIDDEN")
                                    .param("searchField", "CONDITION_DESCRIPTION")
                                    .param("searchWord", "특정 조건")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "layer-dependency-rule-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("슬라이스 크기 (1~100)")
                                                    .optional(),
                                            parameterWithName("architectureIds")
                                                    .description("아키텍처 ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("dependencyTypes")
                                                    .description("의존성 타입 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CONDITION_DESCRIPTION)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("LayerDependencyRule 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].layerDependencyRuleId")
                                                    .description("레이어 의존성 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].architectureId")
                                                    .description("아키텍처 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].fromLayer")
                                                    .description("소스 레이어")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].toLayer")
                                                    .description("타겟 레이어")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].dependencyType")
                                                    .description("의존성 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].conditionDescription")
                                                    .description("조건 설명")
                                                    .type(String.class)
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
                                                    .description("다음 페이지 존재 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.nextCursor")
                                                    .description("다음 페이지 커서")
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
}
