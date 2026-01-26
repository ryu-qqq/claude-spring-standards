package com.ryuqq.adapter.in.rest.checklistitem.controller.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.checklistitem.ChecklistItemApiEndpoints;
import com.ryuqq.adapter.in.rest.checklistitem.mapper.ChecklistItemQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.response.ChecklistItemApiResponseFixture;
import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.application.checklistitem.port.in.SearchChecklistItemsByCursorUseCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ChecklistItemQueryController REST Docs 테스트
 *
 * <p>REST Docs 문서화를 위한 통합 테스트입니다.
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API 요청/응답 필드 문서화
 *   <li>Path Parameter 문서화
 *   <li>Query Parameter 문서화
 *   <li>정상/예외 응답 시나리오 문서화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ChecklistItemQueryController.class)
@DisplayName("ChecklistItemQueryController REST Docs")
class ChecklistItemQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchChecklistItemsByCursorUseCase searchChecklistItemsByCursorUseCase;

    @MockitoBean private ChecklistItemQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/mcp/checklist-items - ChecklistItem 복합 조건 조회")
    class SearchChecklistItemsByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var searchParams =
                    ChecklistItemSearchParams.of(
                            com.ryuqq.application.common.dto.query.CommonCursorParams.of(null, 20),
                            null,
                            null,
                            null,
                            null);
            var result1 =
                    new ChecklistItemResult(
                            1L,
                            1L,
                            1,
                            "Lombok 어노테이션 사용 여부 확인",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "AGG-001-CHECK-1",
                            true,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var result2 =
                    new ChecklistItemResult(
                            2L,
                            1L,
                            2,
                            "Getter 체이닝 사용 여부 확인",
                            "MANUAL",
                            null,
                            null,
                            false,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = ChecklistItemApiResponseFixture.valid();
            var response2 = ChecklistItemApiResponseFixture.validWithoutAutomation();

            var sliceResult = ChecklistItemSliceResult.of(List.of(result1, result2), true);
            var sliceResponse = SliceApiResponse.of(List.of(response1, response2), 20, true, "2");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchChecklistItemsByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(get(ChecklistItemApiEndpoints.CHECKLIST_ITEMS).param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].id").value(1L))
                    .andExpect(jsonPath("$.data.hasNext").value(true))
                    .andDo(
                            document(
                                    "checklist-item-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20, 최대: 100)"),
                                            parameterWithName("ruleIds")
                                                    .description("코딩 규칙 ID 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("checkTypes")
                                                    .description("체크 타입 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("automationTools")
                                                    .description("자동화 도구 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("isCritical")
                                                    .description("필수 여부 필터")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("ChecklistItem 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].id")
                                                    .description("체크리스트 항목 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].sequenceOrder")
                                                    .description("순서")
                                                    .type(Integer.class),
                                            fieldWithPath("data.content[].checkDescription")
                                                    .description("체크 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].checkType")
                                                    .description("체크 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].automationTool")
                                                    .description("자동화 도구")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].automationRuleId")
                                                    .description("자동화 규칙 ID")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.content[].critical")
                                                    .description("필수 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.content[].source")
                                                    .description("체크리스트 소스")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].feedbackId")
                                                    .description("피드백 ID")
                                                    .type(Long.class)
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
                                                    .description("다음 슬라이스 조회를 위한 커서")
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
        @DisplayName("size가 범위를 벗어나면 400 Bad Request 반환")
        void sizeOutOfRange_ShouldReturn400() throws Exception {
            // When & Then
            mockMvc.perform(get(ChecklistItemApiEndpoints.CHECKLIST_ITEMS).param("size", "101"))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "checklist-item-search-by-cursor-size-validation-error",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값")
                                                    .optional(),
                                            parameterWithName("size").description("페이지 크기 (1~100)"),
                                            parameterWithName("ruleIds")
                                                    .description("코딩 규칙 ID 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("checkTypes")
                                                    .description("체크 타입 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("automationTools")
                                                    .description("자동화 도구 필터 (복수)")
                                                    .optional(),
                                            parameterWithName("isCritical")
                                                    .description("필수 여부 필터")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }
    }
}
