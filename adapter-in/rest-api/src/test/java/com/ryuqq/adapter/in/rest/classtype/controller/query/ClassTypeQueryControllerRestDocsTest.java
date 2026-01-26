package com.ryuqq.adapter.in.rest.classtype.controller.query;

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

import com.ryuqq.adapter.in.rest.classtype.ClassTypeApiEndpoints;
import com.ryuqq.adapter.in.rest.classtype.mapper.ClassTypeQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.response.ClassTypeApiResponseFixture;
import com.ryuqq.application.classtype.dto.query.ClassTypeSearchParams;
import com.ryuqq.application.classtype.dto.response.ClassTypeResult;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import com.ryuqq.application.classtype.port.in.SearchClassTypesByCursorUseCase;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ClassTypeQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ClassTypeQueryController.class)
@DisplayName("ClassTypeQueryController REST Docs")
class ClassTypeQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchClassTypesByCursorUseCase searchClassTypesByCursorUseCase;

    @MockitoBean private ClassTypeQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/templates/class-types - ClassType 복합 조건 조회")
    class SearchClassTypesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ClassTypeSearchParams.of(null, List.of(1L), null, null, null, null, 20);
            // params: ids, categoryIds, architectureIds, searchField, searchWord, cursor, size
            var result1 =
                    new ClassTypeResult(
                            1L,
                            1L,
                            "AGGREGATE",
                            "Aggregate",
                            "도메인 Aggregate Root 클래스",
                            1,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ClassTypeApiResponseFixture.valid();

            var sliceMeta = SliceMeta.withCursor("2", 20, true);
            var sliceResult = new ClassTypeSliceResult(List.of(result1), sliceMeta);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchClassTypesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(ClassTypeApiEndpoints.CLASS_TYPES).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "class-type-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("categoryIds")
                                                    .description("Category ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (CODE, NAME, DESCRIPTION)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어 (부분 일치, 최대 255자)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("ClassType 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("ClassType ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].categoryId")
                                                    .description("ClassTypeCategory ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].code")
                                                    .description("클래스 타입 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].name")
                                                    .description("클래스 타입 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("클래스 타입 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].orderIndex")
                                                    .description("정렬 순서")
                                                    .type(Integer.class),
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
