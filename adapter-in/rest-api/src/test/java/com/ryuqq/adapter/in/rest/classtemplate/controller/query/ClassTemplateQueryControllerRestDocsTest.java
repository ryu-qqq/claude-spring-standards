package com.ryuqq.adapter.in.rest.classtemplate.controller.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.classtemplate.ClassTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.classtemplate.mapper.ClassTemplateQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.response.ClassTemplateApiResponseFixture;
import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateResult;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.application.classtemplate.port.in.SearchClassTemplatesByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ClassTemplateQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ClassTemplateQueryController.class)
@DisplayName("ClassTemplateQueryController REST Docs")
class ClassTemplateQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchClassTemplatesByCursorUseCase searchClassTemplatesByCursorUseCase;

    @MockitoBean private ClassTemplateQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/class-templates - ClassTemplate 복합 조건 조회")
    class SearchClassTemplatesByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ClassTemplateSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            null,
                            null);
            var result1 =
                    new ClassTemplateResult(
                            1L,
                            1L,
                            1L, // classTypeId (AGGREGATE)
                            "public class {ClassName} { ... }",
                            ".*Aggregate",
                            "Aggregate Root 클래스 템플릿",
                            List.of("@Entity"),
                            List.of("@Data"),
                            List.of(),
                            List.of(),
                            List.of(),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ClassTemplateApiResponseFixture.valid();

            var sliceResult = new ClassTemplateSliceResult(List.of(result1), true, 2L);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchClassTemplatesByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(ClassTemplateApiEndpoints.CLASS_TEMPLATES).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "class-template-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 ID")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("structureIds")
                                                    .description("패키지 구조 ID 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("classTypeIds")
                                                    .description("클래스 타입 ID 필터 (복수 선택 가능)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("ClassTemplate 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].classTemplateId")
                                                    .description("클래스 템플릿 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].classTypeId")
                                                    .description("클래스 타입 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].templateCode")
                                                    .description("템플릿 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].namingPattern")
                                                    .description("네이밍 패턴")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].description")
                                                    .description("템플릿 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].requiredAnnotations")
                                                    .description("필수 어노테이션 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].forbiddenAnnotations")
                                                    .description("금지 어노테이션 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].requiredInterfaces")
                                                    .description("필수 인터페이스 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].forbiddenInheritance")
                                                    .description("금지 상속 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].requiredMethods")
                                                    .description("필수 메서드 목록")
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
    }
}
