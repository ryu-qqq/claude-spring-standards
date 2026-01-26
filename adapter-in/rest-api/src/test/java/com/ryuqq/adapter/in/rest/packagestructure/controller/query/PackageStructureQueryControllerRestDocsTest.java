package com.ryuqq.adapter.in.rest.packagestructure.controller.query;

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
import com.ryuqq.adapter.in.rest.fixture.response.PackageStructureApiResponseFixture;
import com.ryuqq.adapter.in.rest.packagestructure.PackageStructureApiEndpoints;
import com.ryuqq.adapter.in.rest.packagestructure.mapper.PackageStructureQueryApiMapper;
import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureResult;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.application.packagestructure.port.in.SearchPackageStructuresByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * PackageStructureQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(PackageStructureQueryController.class)
@DisplayName("PackageStructureQueryController REST Docs")
class PackageStructureQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private SearchPackageStructuresByCursorUseCase searchPackageStructuresByCursorUseCase;

    @MockitoBean private PackageStructureQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/package-structures - PackageStructure 복합 조건 조회")
    class SearchPackageStructuresByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    PackageStructureSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            null);
            var result1 =
                    new PackageStructureResult(
                            1L,
                            1L,
                            "{base}.domain.{bc}.aggregate",
                            List.of("CLASS", "RECORD"),
                            ".*Aggregate",
                            "Aggregate",
                            "Aggregate Root 패키지",
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = PackageStructureApiResponseFixture.valid();

            var sliceResult = new PackageStructureSliceResult(List.of(result1), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchPackageStructuresByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(PackageStructureApiEndpoints.PACKAGE_STRUCTURES)
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "package-structure-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("moduleIds")
                                                    .description("모듈 ID 필터 (복수 선택 가능)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("PackageStructure 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].packageStructureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].pathPattern")
                                                    .description("경로 패턴")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].allowedClassTypes")
                                                    .description("허용 클래스 타입 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].namingPattern")
                                                    .description("네이밍 패턴")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].namingSuffix")
                                                    .description("네이밍 접미사")
                                                    .type(String.class)
                                                    .optional(),
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
