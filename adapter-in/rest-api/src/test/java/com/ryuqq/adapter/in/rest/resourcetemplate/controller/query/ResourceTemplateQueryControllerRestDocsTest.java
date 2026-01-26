package com.ryuqq.adapter.in.rest.resourcetemplate.controller.query;

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
import com.ryuqq.adapter.in.rest.fixture.response.ResourceTemplateApiResponseFixture;
import com.ryuqq.adapter.in.rest.resourcetemplate.ResourceTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.resourcetemplate.mapper.ResourceTemplateQueryApiMapper;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateResult;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.application.resourcetemplate.port.in.SearchResourceTemplatesByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ResourceTemplateQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ResourceTemplateQueryController.class)
@DisplayName("ResourceTemplateQueryController REST Docs")
class ResourceTemplateQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private SearchResourceTemplatesByCursorUseCase searchResourceTemplatesByCursorUseCase;

    @MockitoBean private ResourceTemplateQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/resource-templates - ResourceTemplate 복합 조건 조회")
    class SearchResourceTemplatesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ResourceTemplateSearchParams.of(
                            CommonCursorParams.of(null, 20),
                            List.of(1L),
                            List.of("CONFIG"),
                            List.of("YAML"));
            var result1 =
                    new ResourceTemplateResult(
                            1L,
                            1L,
                            "DOMAIN",
                            "src/main/java/Order.java",
                            "JAVA",
                            "Order Aggregate",
                            "public class Order {}",
                            true,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ResourceTemplateApiResponseFixture.valid();

            var sliceResult = new ResourceTemplateSliceResult(List.of(result1), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchResourceTemplatesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(ResourceTemplateApiEndpoints.RESOURCE_TEMPLATES)
                                    .param("moduleIds", "1")
                                    .param("categories", "CONFIG")
                                    .param("fileTypes", "YAML")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "resource-template-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("moduleIds")
                                                    .description("모듈 ID 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("categories")
                                                    .description("카테고리 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("fileTypes")
                                                    .description("파일 타입 필터 (복수)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("ResourceTemplate 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].resourceTemplateId")
                                                    .description("리소스 템플릿 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].category")
                                                    .description("카테고리")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].filePath")
                                                    .description("파일 경로")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].fileType")
                                                    .description("파일 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].templateContent")
                                                    .description("템플릿 콘텐츠")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].required")
                                                    .description("필수 여부")
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
