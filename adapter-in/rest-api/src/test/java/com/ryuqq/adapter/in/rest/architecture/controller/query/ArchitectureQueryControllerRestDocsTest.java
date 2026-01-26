package com.ryuqq.adapter.in.rest.architecture.controller.query;

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

import com.ryuqq.adapter.in.rest.architecture.ArchitectureApiEndpoints;
import com.ryuqq.adapter.in.rest.architecture.mapper.ArchitectureQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.response.ArchitectureApiResponseFixture;
import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.architecture.dto.response.ArchitectureResult;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.application.architecture.port.in.SearchArchitecturesByCursorUseCase;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ArchitectureQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ArchitectureQueryController.class)
@DisplayName("ArchitectureQueryController REST Docs")
class ArchitectureQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchArchitecturesByCursorUseCase searchArchitecturesByCursorUseCase;

    @MockitoBean private ArchitectureQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/architectures - Architecture 복합 조건 조회")
    class SearchArchitecturesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ArchitectureSearchParams.of(
                            CommonCursorParams.of(null, 20), Collections.emptyList());
            var result1 =
                    new ArchitectureResult(
                            1L,
                            1L,
                            "Hexagonal Architecture",
                            "HEXAGONAL",
                            "Ports and Adapters 패턴",
                            List.of("의존성 역전"),
                            List.of(),
                            false,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ArchitectureApiResponseFixture.valid();

            var sliceResult =
                    new ArchitectureSliceResult(
                            List.of(result1),
                            com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1));
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchArchitecturesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(ArchitectureApiEndpoints.ARCHITECTURES).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "architecture-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("슬라이스 크기 (1~100)"),
                                            parameterWithName("techStackIds")
                                                    .description("TechStack ID 필터 (복수 선택 가능)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("Architecture 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("Architecture ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].techStackId")
                                                    .description("기술 스택 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].name")
                                                    .description("아키텍처 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].patternType")
                                                    .description("패턴 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].patternDescription")
                                                    .description("패턴 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].patternPrinciples")
                                                    .description("패턴 원칙 목록")
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

        @Test
        @DisplayName("techStackIds 필터로 조회 시 200 OK 반환")
        void withTechStackIds_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ArchitectureSearchParams.of(CommonCursorParams.of(null, 20), List.of(1L, 2L));
            var result1 =
                    new ArchitectureResult(
                            1L,
                            1L,
                            "Hexagonal Architecture",
                            "HEXAGONAL",
                            "Ports and Adapters 패턴",
                            List.of("의존성 역전"),
                            List.of(),
                            false,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ArchitectureApiResponseFixture.valid();

            var sliceResult =
                    new ArchitectureSliceResult(
                            List.of(result1),
                            com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, false, 1));
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, false, null);

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchArchitecturesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(ArchitectureApiEndpoints.ARCHITECTURES)
                                    .param("size", "20")
                                    .param("techStackIds", "1", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }
    }
}
