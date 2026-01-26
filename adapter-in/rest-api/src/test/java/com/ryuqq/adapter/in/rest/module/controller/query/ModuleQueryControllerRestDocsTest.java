package com.ryuqq.adapter.in.rest.module.controller.query;

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
import com.ryuqq.adapter.in.rest.fixture.response.ModuleApiResponseFixture;
import com.ryuqq.adapter.in.rest.module.ModuleApiEndpoints;
import com.ryuqq.adapter.in.rest.module.mapper.ModuleQueryApiMapper;
import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.application.module.dto.response.ModuleResult;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.port.in.GetModuleTreeUseCase;
import com.ryuqq.application.module.port.in.SearchModulesByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ModuleQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ModuleQueryController.class)
@DisplayName("ModuleQueryController REST Docs")
class ModuleQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchModulesByCursorUseCase searchModulesByCursorUseCase;

    @MockitoBean private GetModuleTreeUseCase getModuleTreeUseCase;

    @MockitoBean private ModuleQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/modules - Module 목록 조회")
    class GetAllModules {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ModuleSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            List.of(1L));
            var result1 =
                    new ModuleResult(
                            1L,
                            1L,
                            null,
                            "adapter-in-rest-api",
                            "REST API Adapter",
                            "adapter-in/rest-api",
                            ":adapter-in:rest-api",
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ModuleApiResponseFixture.valid();

            var sliceResult = new ModuleSliceResult(List.of(result1), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchModulesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(ModuleApiEndpoints.MODULES)
                                    .param("layerIds", "1")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "module-get-all",
                                    queryParameters(
                                            parameterWithName("layerIds")
                                                    .description("레이어 ID 필터 (복수 선택 가능)"),
                                            parameterWithName("cursorId")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (1~100)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("Module 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].layerId")
                                                    .description("레이어 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].parentModuleId")
                                                    .description("부모 모듈 ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].name")
                                                    .description("모듈 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].modulePath")
                                                    .description("모듈 경로")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].buildIdentifier")
                                                    .description("빌드 식별자")
                                                    .type(String.class),
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
