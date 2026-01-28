package com.ryuqq.adapter.in.rest.packagepurpose.controller.query;

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
import com.ryuqq.adapter.in.rest.packagepurpose.PackagePurposeApiEndpoints;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeApiResponse;
import com.ryuqq.adapter.in.rest.packagepurpose.mapper.PackagePurposeQueryApiMapper;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeResult;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.application.packagepurpose.port.in.SearchPackagePurposesByCursorUseCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * PackagePurposeQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(PackagePurposeQueryController.class)
@DisplayName("PackagePurposeQueryController REST Docs")
class PackagePurposeQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchPackagePurposesByCursorUseCase searchPackagePurposesByCursorUseCase;

    @MockitoBean private PackagePurposeQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/ref/package-purposes - PackagePurpose 복합 조건 조회")
    class SearchPackagePurposesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    PackagePurposeSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            null,
                            null,
                            null);
            var result =
                    new PackagePurposeResult(
                            1L,
                            1L,
                            "AGGREGATE",
                            "Aggregate Root",
                            "Description",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var response =
                    new PackagePurposeApiResponse(
                            1L,
                            1L,
                            "AGGREGATE",
                            "Aggregate Root",
                            "Description",
                            "2024-01-01T00:00:00Z",
                            "2024-01-01T00:00:00Z");

            var sliceResult = new PackagePurposeSliceResult(List.of(result), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchPackagePurposesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(PackagePurposeApiEndpoints.BASE).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "package-purpose-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("structureIds")
                                                    .description("패키지 구조 ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME, DESCRIPTION)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("PackagePurpose 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.content[].packagePurposeId")
                                                    .description("PackagePurpose ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].structureId")
                                                    .description("구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].code")
                                                    .description("목적 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].name")
                                                    .description("목적 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("설명")
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
