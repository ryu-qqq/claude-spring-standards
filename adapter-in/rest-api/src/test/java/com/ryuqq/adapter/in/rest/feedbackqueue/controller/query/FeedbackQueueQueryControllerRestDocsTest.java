package com.ryuqq.adapter.in.rest.feedbackqueue.controller.query;

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
import com.ryuqq.adapter.in.rest.feedbackqueue.FeedbackQueueApiEndpoints;
import com.ryuqq.adapter.in.rest.feedbackqueue.mapper.FeedbackQueueQueryApiMapper;
import com.ryuqq.adapter.in.rest.fixture.request.SearchFeedbacksCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.response.FeedbackQueueApiResponseFixture;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.port.in.SearchFeedbacksByCursorUseCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * FeedbackQueueQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(FeedbackQueueQueryController.class)
@DisplayName("FeedbackQueueQueryController REST Docs")
class FeedbackQueueQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private SearchFeedbacksByCursorUseCase searchFeedbacksByCursorUseCase;

    @MockitoBean private FeedbackQueueQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET / - FeedbackQueue 복합 조건 조회 (커서 기반)")
    class SearchFeedbacksByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var request = SearchFeedbacksCursorApiRequestFixture.valid();
            var searchParams =
                    FeedbackQueueSearchParams.of(
                            CommonCursorParams.of(request.cursor(), request.size()),
                            request.statuses(),
                            request.targetTypes(),
                            request.feedbackTypes(),
                            request.riskLevels(),
                            request.actions());
            var result1 =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "PENDING_LLM",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var response1 = FeedbackQueueApiResponseFixture.valid();

            var sliceResult = FeedbackQueueSliceResult.of(List.of(result1), true);
            var sliceResponse = SliceApiResponse.of(List.of(response1), 20, true, "1");

            given(mapper.toSearchParams(any())).willReturn(searchParams);
            given(searchFeedbacksByCursorUseCase.execute(any())).willReturn(sliceResult);
            given(mapper.toSliceResponse(any())).willReturn(sliceResponse);

            // When & Then
            mockMvc.perform(
                            get(FeedbackQueueApiEndpoints.BASE)
                                    .param("statuses", "PENDING", "LLM_APPROVED")
                                    .param("targetTypes", "CODING_RULE")
                                    .param("feedbackTypes", "ADD", "MODIFY")
                                    .param("riskLevels", "SAFE", "MEDIUM")
                                    .param("actions", "LLM_APPROVE", "HUMAN_REJECT")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andDo(
                            document(
                                    "feedback-queue-search-by-cursor",
                                    queryParameters(
                                            parameterWithName("cursor")
                                                    .description("커서 값 (마지막 항목의 ID)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("슬라이스 크기 (1~100)")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description("상태 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("targetTypes")
                                                    .description("대상 타입 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("feedbackTypes")
                                                    .description("피드백 타입 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("riskLevels")
                                                    .description("리스크 레벨 필터 (복수 선택 가능)")
                                                    .optional(),
                                            parameterWithName("actions")
                                                    .description("처리 액션 필터 (복수 선택 가능)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.content")
                                                    .description("FeedbackQueue 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("data.content[].feedbackQueueId")
                                                    .description("피드백 큐 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].targetType")
                                                    .description("대상 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.content[].feedbackType")
                                                    .description("피드백 유형")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].riskLevel")
                                                    .description("리스크 레벨")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].payload")
                                                    .description("피드백 페이로드")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].status")
                                                    .description("현재 상태")
                                                    .type(String.class),
                                            fieldWithPath("data.content[].reviewNotes")
                                                    .description("리뷰 노트")
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
