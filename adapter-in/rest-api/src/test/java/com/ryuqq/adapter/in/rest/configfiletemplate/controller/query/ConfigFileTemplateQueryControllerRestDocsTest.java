package com.ryuqq.adapter.in.rest.configfiletemplate.controller.query;

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
import com.ryuqq.adapter.in.rest.configfiletemplate.ConfigFileTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.SearchConfigFileTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.configfiletemplate.mapper.ConfigFileTemplateQueryApiMapper;
import com.ryuqq.adapter.in.rest.fixture.response.ConfigFileTemplateApiResponseFixture;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateResult;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import com.ryuqq.application.configfiletemplate.port.in.SearchConfigFileTemplatesByCursorUseCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ConfigFileTemplateQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ConfigFileTemplateQueryController.class)
@DisplayName("ConfigFileTemplateQueryController REST Docs")
class ConfigFileTemplateQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private SearchConfigFileTemplatesByCursorUseCase searchConfigFileTemplatesByCursorUseCase;

    @MockitoBean private ConfigFileTemplateQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/templates/config-files - ConfigFileTemplate 목록 조회")
    class GetAllConfigFileTemplates {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ConfigFileTemplateSearchParams.of(
                            CommonCursorParams.of(null, 20), null, null, null, null, null);
            var result1 =
                    new ConfigFileTemplateResult(
                            1L,
                            1L,
                            1L,
                            "CLAUDE",
                            ".claude/CLAUDE.md",
                            "CLAUDE.md",
                            "# Project Configuration",
                            "MAIN_CONFIG",
                            "Claude Code 메인 설정 파일",
                            "{\"project_name\": \"string\"}",
                            0,
                            true,
                            false,
                            Instant.parse("2024-01-15T10:30:00Z"),
                            Instant.parse("2024-01-15T10:30:00Z"));
            var response1 = ConfigFileTemplateApiResponseFixture.valid();

            var sliceMeta = com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1);
            var sliceResult = new ConfigFileTemplateSliceResult(List.of(result1), sliceMeta);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any(SearchConfigFileTemplatesCursorApiRequest.class)))
                    .willReturn(searchParams);
            given(searchConfigFileTemplatesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(ConfigFileTemplateApiEndpoints.CONFIG_FILES).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "config-file-template-get-all",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("toolTypes")
                                                    .description(
                                                            "도구 타입 필터 (CLAUDE, CURSOR, COPILOT)")
                                                    .optional(),
                                            parameterWithName("techStackIds")
                                                    .description("TechStack ID 필터 목록")
                                                    .optional(),
                                            parameterWithName("architectureIds")
                                                    .description("Architecture ID 필터 목록")
                                                    .optional(),
                                            parameterWithName("categories")
                                                    .description("카테고리 필터 목록")
                                                    .optional(),
                                            parameterWithName("isRequired")
                                                    .description("필수 파일 여부 필터")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("ConfigFileTemplate 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("ConfigFileTemplate ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].techStackId")
                                                    .description("TechStack ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].architectureId")
                                                    .description("Architecture ID")
                                                    .type(Long.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].toolType")
                                                    .description("도구 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].filePath")
                                                    .description("파일 경로")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].fileName")
                                                    .description("파일명")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].content")
                                                    .description("파일 내용")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].category")
                                                    .description("카테고리")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].description")
                                                    .description("템플릿 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].variables")
                                                    .description("치환 가능한 변수 정의 (JSON)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].displayOrder")
                                                    .description("정렬 순서")
                                                    .type(Integer.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].isRequired")
                                                    .description("필수 파일 여부")
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
