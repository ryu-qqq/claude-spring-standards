package com.ryuqq.adapter.in.rest.convention.controller.query;

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
import com.ryuqq.adapter.in.rest.convention.ConventionApiEndpoints;
import com.ryuqq.adapter.in.rest.convention.mapper.ConventionQueryApiMapper;
import com.ryuqq.adapter.in.rest.fixture.response.ConventionApiResponseFixture;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.application.convention.dto.response.ConventionResult;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.application.convention.port.in.SearchConventionsByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ConventionQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ConventionQueryController.class)
@DisplayName("ConventionQueryController REST Docs")
class ConventionQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchConventionsByCursorUseCase searchConventionsByCursorUseCase;

    @MockitoBean private ConventionQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/conventions - Convention 목록 조회")
    class GetAllConventions {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ConventionSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20));
            var result1 =
                    new ConventionResult(
                            1L,
                            1L,
                            "1.0.0",
                            "Domain Layer Convention",
                            true,
                            false,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ConventionApiResponseFixture.valid();

            var sliceMeta = com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1);
            var sliceResult = new ConventionSliceResult(List.of(result1), sliceMeta);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchConventionsByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(ConventionApiEndpoints.CONVENTIONS).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "convention-get-all",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (1~100)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("Convention 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("Convention ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].version")
                                                    .description("버전")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("설명")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].active")
                                                    .description("활성화 여부")
                                                    .type(Boolean.class),
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
