package com.ryuqq.adapter.in.rest.techstack.controller.query;

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
import com.ryuqq.adapter.in.rest.fixture.response.TechStackApiResponseFixture;
import com.ryuqq.adapter.in.rest.techstack.TechStackApiEndpoints;
import com.ryuqq.adapter.in.rest.techstack.dto.request.SearchTechStacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.techstack.mapper.TechStackQueryApiMapper;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.application.techstack.dto.response.TechStackResult;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.application.techstack.port.in.SearchTechStacksByCursorUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * TechStackQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(TechStackQueryController.class)
@DisplayName("TechStackQueryController REST Docs")
class TechStackQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchTechStacksByCursorUseCase searchTechStacksByCursorUseCase;

    @MockitoBean private TechStackQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/tech-stacks - TechStack 목록 조회")
    class GetAllTechStacks {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    TechStackSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            null,
                            null);
            var result1 =
                    new TechStackResult(
                            1L,
                            "Spring Boot 3.5 with Java 21",
                            "ACTIVE",
                            "JAVA",
                            "21",
                            List.of("records", "sealed-classes"),
                            "SPRING_BOOT",
                            "3.5.0",
                            List.of("spring-web", "spring-data-jpa"),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            List.of(),
                            false,
                            java.time.Instant.parse("2024-01-01T00:00:00Z"),
                            java.time.Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = TechStackApiResponseFixture.valid();

            var sliceMeta = com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1);
            var sliceResult = new TechStackSliceResult(List.of(result1), sliceMeta);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "2");

            given(mapper.toSearchParams(any(SearchTechStacksCursorApiRequest.class)))
                    .willReturn(searchParams);
            given(searchTechStacksByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(TechStackApiEndpoints.TECH_STACKS).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "tech-stack-get-all",
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
                                                    .description("TechStack 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("TechStack ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].name")
                                                    .description("TechStack 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].status")
                                                    .description("상태")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].languageType")
                                                    .description("언어 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].languageVersion")
                                                    .description("언어 버전")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].languageFeatures")
                                                    .description("언어 기능 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].frameworkType")
                                                    .description("프레임워크 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].frameworkVersion")
                                                    .description("프레임워크 버전")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].frameworkModules")
                                                    .description("프레임워크 모듈 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].platformType")
                                                    .description("플랫폼 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].runtimeEnvironment")
                                                    .description("런타임 환경")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].buildToolType")
                                                    .description("빌드 도구 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].buildConfigFile")
                                                    .description("빌드 설정 파일")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].referenceLinks")
                                                    .description("참조 링크 목록")
                                                    .type(java.util.List.class)
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
