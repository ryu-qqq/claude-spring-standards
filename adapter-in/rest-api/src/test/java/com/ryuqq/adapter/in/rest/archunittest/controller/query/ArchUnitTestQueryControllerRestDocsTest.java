package com.ryuqq.adapter.in.rest.archunittest.controller.query;

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

import com.ryuqq.adapter.in.rest.archunittest.ArchUnitTestApiEndpoints;
import com.ryuqq.adapter.in.rest.archunittest.mapper.ArchUnitTestQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.SearchArchUnitTestsCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.response.ArchUnitTestApiResponseFixture;
import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestResult;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.application.archunittest.port.in.SearchArchUnitTestsByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ArchUnitTestQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ArchUnitTestQueryController.class)
@DisplayName("ArchUnitTestQueryController REST Docs")
class ArchUnitTestQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchArchUnitTestsByCursorUseCase searchArchUnitTestsByCursorUseCase;

    @MockitoBean private ArchUnitTestQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/arch-unit-tests - ArchUnitTest 복합 조건 조회")
    class SearchArchUnitTestsByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var request = SearchArchUnitTestsCursorApiRequestFixture.valid();
            var searchParams =
                    ArchUnitTestSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(
                                    request.cursor(), request.size()),
                            request.structureIds(),
                            request.searchField(),
                            request.searchWord(),
                            request.severities());
            var result1 =
                    new ArchUnitTestResult(
                            1L,
                            1L,
                            "ARCH-001",
                            "Lombok 사용 금지 테스트",
                            "Description",
                            "DomainLayerArchUnitTest",
                            "shouldNotUseLombok",
                            "testCode",
                            "BLOCKER",
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ArchUnitTestApiResponseFixture.valid();

            var sliceResult = new ArchUnitTestSliceResult(List.of(result1), 20, true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchArchUnitTestsByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(ArchUnitTestApiEndpoints.BASE)
                                    .param("structureIds", "1", "2")
                                    .param("searchField", "CODE")
                                    .param("searchWord", "AGG-001")
                                    .param("severities", "BLOCKER", "CRITICAL")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "arch-unit-test-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("슬라이스 크기 (1~100)")
                                                    .optional(),
                                            parameterWithName("structureIds")
                                                    .description("패키지 구조 ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (CODE, NAME, DESCRIPTION,"
                                                                    + " TEST_CLASS_NAME,"
                                                                    + " TEST_METHOD_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("severities")
                                                    .description("심각도 필터 (복수 선택 가능)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("ArchUnitTest 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].archUnitTestId")
                                                    .description("ArchUnitTest ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].code")
                                                    .description("테스트 코드 식별자")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].name")
                                                    .description("테스트 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("테스트 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].testClassName")
                                                    .description("테스트 클래스 이름")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].testMethodName")
                                                    .description("테스트 메서드 이름")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].testCode")
                                                    .description("테스트 코드 내용")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].severity")
                                                    .description("심각도")
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
