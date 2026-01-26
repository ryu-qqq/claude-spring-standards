package com.ryuqq.adapter.in.rest.onboardingcontext.controller.query;

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
import com.ryuqq.adapter.in.rest.fixture.response.OnboardingContextApiResponseFixture;
import com.ryuqq.adapter.in.rest.onboardingcontext.OnboardingContextApiEndpoints;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.SearchOnboardingContextsCursorApiRequest;
import com.ryuqq.adapter.in.rest.onboardingcontext.mapper.OnboardingContextQueryApiMapper;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.onboardingcontext.dto.query.OnboardingContextSearchParams;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextResult;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextSliceResult;
import com.ryuqq.application.onboardingcontext.port.in.SearchOnboardingContextsByCursorUseCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * OnboardingContextQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(OnboardingContextQueryController.class)
@DisplayName("OnboardingContextQueryController REST Docs")
class OnboardingContextQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private SearchOnboardingContextsByCursorUseCase searchOnboardingContextsByCursorUseCase;

    @MockitoBean private OnboardingContextQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/templates/onboarding-contexts - OnboardingContext 목록 조회")
    class GetAllOnboardingContexts {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    OnboardingContextSearchParams.of(
                            CommonCursorParams.of(null, 20), null, null, null);
            var result1 =
                    new OnboardingContextResult(
                            1L,
                            1L,
                            1L,
                            "SUMMARY",
                            "프로젝트 개요",
                            "# 프로젝트 개요\n\n이 프로젝트는 Spring Boot 기반입니다.",
                            0,
                            false,
                            Instant.parse("2024-01-15T10:30:00Z"),
                            Instant.parse("2024-01-15T10:30:00Z"));
            var response1 = OnboardingContextApiResponseFixture.valid();

            var sliceMeta = com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1);
            var sliceResult = new OnboardingContextSliceResult(List.of(result1), sliceMeta);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any(SearchOnboardingContextsCursorApiRequest.class)))
                    .willReturn(searchParams);
            given(searchOnboardingContextsByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(OnboardingContextApiEndpoints.ONBOARDING_CONTEXTS)
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "onboarding-context-get-all",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("techStackIds")
                                                    .description("TechStack ID 필터 목록")
                                                    .optional(),
                                            parameterWithName("architectureIds")
                                                    .description("Architecture ID 필터 목록")
                                                    .optional(),
                                            parameterWithName("contextTypes")
                                                    .description(
                                                            "컨텍스트 타입 필터 (SUMMARY, ZERO_TOLERANCE,"
                                                                    + " RULES_INDEX, MCP_USAGE)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("OnboardingContext 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("OnboardingContext ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].techStackId")
                                                    .description("TechStack ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].architectureId")
                                                    .description("Architecture ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].contextType")
                                                    .description("컨텍스트 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].title")
                                                    .description("컨텍스트 제목")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].content")
                                                    .description("컨텍스트 내용")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].priority")
                                                    .description("온보딩 시 표시 순서")
                                                    .type(Integer.class)
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
}
