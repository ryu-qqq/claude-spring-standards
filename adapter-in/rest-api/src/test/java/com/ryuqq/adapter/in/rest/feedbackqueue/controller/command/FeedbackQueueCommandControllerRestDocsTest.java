package com.ryuqq.adapter.in.rest.feedbackqueue.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.feedbackqueue.FeedbackQueueApiEndpoints;
import com.ryuqq.adapter.in.rest.feedbackqueue.mapper.FeedbackQueueCommandApiMapper;
import com.ryuqq.adapter.in.rest.feedbackqueue.mapper.FeedbackQueueQueryApiMapper;
import com.ryuqq.adapter.in.rest.fixture.request.CreateFeedbackApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.RejectFeedbackApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.response.FeedbackQueueApiResponseFixture;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.port.in.CreateFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.MergeFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.ProcessFeedbackUseCase;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * FeedbackQueueCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(FeedbackQueueCommandController.class)
@DisplayName("FeedbackQueueCommandController REST Docs")
class FeedbackQueueCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateFeedbackUseCase createFeedbackUseCase;

    @MockitoBean private ProcessFeedbackUseCase processFeedbackUseCase;

    @MockitoBean private MergeFeedbackUseCase mergeFeedbackUseCase;

    @MockitoBean private FeedbackQueueCommandApiMapper commandMapper;

    @MockitoBean private FeedbackQueueQueryApiMapper queryMapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/templates/feedback-queue - 피드백 생성")
    class CreateFeedback {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateFeedbackApiRequestFixture.valid();
            var command =
                    new CreateFeedbackCommand(
                            "CODING_RULE", 1L, "CREATE", "{\"code\":\"AGG-001\"}");
            Long createdId = 1L;

            given(commandMapper.toCommand(any())).willReturn(command);
            given(createFeedbackUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(FeedbackQueueApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.feedbackQueueId").value(createdId))
                    .andDo(
                            document(
                                    "feedback-queue-create",
                                    requestFields(
                                            fieldWithPath("targetType")
                                                    .description(
                                                            "대상 타입 (CODING_RULE, CLASS_TEMPLATE 등)")
                                                    .type(String.class),
                                            fieldWithPath("targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("feedbackType")
                                                    .description("피드백 유형 (CREATE, UPDATE, DELETE)")
                                                    .type(String.class),
                                            fieldWithPath("payload")
                                                    .description("피드백 페이로드 (JSON 형식)")
                                                    .type(String.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.feedbackQueueId")
                                                    .description("생성된 FeedbackQueue ID")
                                                    .type(Long.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() throws Exception {
            // Given
            var request = CreateFeedbackApiRequestFixture.invalidWithBlankTargetType();

            // When & Then
            mockMvc.perform(
                            post(FeedbackQueueApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("feedback-queue-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/llm-approve - LLM 1차 승인")
    class LlmApprove {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long feedbackQueueId = 1L;
            var command =
                    ProcessFeedbackCommand.approve(feedbackQueueId, FeedbackAction.LLM_APPROVE);
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "LLM_APPROVED",
                            null,
                            Instant.now(),
                            Instant.now());
            var response = FeedbackQueueApiResponseFixture.llmApproved();

            given(commandMapper.toLlmApproveCommand(feedbackQueueId)).willReturn(command);
            given(processFeedbackUseCase.execute(any())).willReturn(result);
            given(queryMapper.toResponse(any())).willReturn(response);

            // When & Then
            mockMvc.perform(patch(FeedbackQueueApiEndpoints.LLM_APPROVE, feedbackQueueId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.feedbackQueueId").value(1L))
                    .andExpect(jsonPath("$.data.status").value("LLM_APPROVED"))
                    .andDo(
                            document(
                                    "feedback-queue-llm-approve",
                                    pathParameters(
                                            parameterWithName("feedbackQueueId")
                                                    .description("FeedbackQueue ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.feedbackQueueId")
                                                    .description("피드백 큐 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.targetType")
                                                    .description("대상 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.feedbackType")
                                                    .description("피드백 유형")
                                                    .type(String.class),
                                            fieldWithPath("data.riskLevel")
                                                    .description("리스크 레벨")
                                                    .type(String.class),
                                            fieldWithPath("data.payload")
                                                    .description("피드백 페이로드")
                                                    .type(String.class),
                                            fieldWithPath("data.status")
                                                    .description("현재 상태")
                                                    .type(String.class),
                                            fieldWithPath("data.reviewNotes")
                                                    .description("리뷰 노트")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/llm-reject - LLM 1차 거절")
    class LlmReject {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long feedbackQueueId = 1L;
            var request = RejectFeedbackApiRequestFixture.valid();
            var command =
                    ProcessFeedbackCommand.reject(
                            feedbackQueueId, FeedbackAction.LLM_REJECT, request.reviewNotes());
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "LLM_REJECTED",
                            request.reviewNotes(),
                            Instant.now(),
                            Instant.now());
            var response = FeedbackQueueApiResponseFixture.llmRejected();

            given(commandMapper.toLlmRejectCommand(any(), any())).willReturn(command);
            given(processFeedbackUseCase.execute(any())).willReturn(result);
            given(queryMapper.toResponse(any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            patch(FeedbackQueueApiEndpoints.LLM_REJECT, feedbackQueueId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("LLM_REJECTED"))
                    .andDo(
                            document(
                                    "feedback-queue-llm-reject",
                                    pathParameters(
                                            parameterWithName("feedbackQueueId")
                                                    .description("FeedbackQueue ID")),
                                    requestFields(
                                            fieldWithPath("reviewNotes")
                                                    .description("거절 사유")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.feedbackQueueId")
                                                    .description("피드백 큐 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.targetType")
                                                    .description("대상 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.feedbackType")
                                                    .description("피드백 유형")
                                                    .type(String.class),
                                            fieldWithPath("data.riskLevel")
                                                    .description("리스크 레벨")
                                                    .type(String.class),
                                            fieldWithPath("data.payload")
                                                    .description("피드백 페이로드")
                                                    .type(String.class),
                                            fieldWithPath("data.status")
                                                    .description("현재 상태")
                                                    .type(String.class),
                                            fieldWithPath("data.reviewNotes")
                                                    .description("리뷰 노트 (거절 사유)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/human-approve - Human 2차 승인")
    class HumanApprove {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long feedbackQueueId = 1L;
            var command =
                    ProcessFeedbackCommand.approve(feedbackQueueId, FeedbackAction.HUMAN_APPROVE);
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "MEDIUM",
                            "{\"code\":\"AGG-001\"}",
                            "HUMAN_APPROVED",
                            null,
                            Instant.now(),
                            Instant.now());
            var response = FeedbackQueueApiResponseFixture.humanApproved();

            given(commandMapper.toHumanApproveCommand(feedbackQueueId)).willReturn(command);
            given(processFeedbackUseCase.execute(any())).willReturn(result);
            given(queryMapper.toResponse(any())).willReturn(response);

            // When & Then
            mockMvc.perform(patch(FeedbackQueueApiEndpoints.HUMAN_APPROVE, feedbackQueueId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("HUMAN_APPROVED"))
                    .andDo(
                            document(
                                    "feedback-queue-human-approve",
                                    pathParameters(
                                            parameterWithName("feedbackQueueId")
                                                    .description("FeedbackQueue ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.feedbackQueueId")
                                                    .description("피드백 큐 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.targetType")
                                                    .description("대상 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.feedbackType")
                                                    .description("피드백 유형")
                                                    .type(String.class),
                                            fieldWithPath("data.riskLevel")
                                                    .description("리스크 레벨")
                                                    .type(String.class),
                                            fieldWithPath("data.payload")
                                                    .description("피드백 페이로드")
                                                    .type(String.class),
                                            fieldWithPath("data.status")
                                                    .description("현재 상태")
                                                    .type(String.class),
                                            fieldWithPath("data.reviewNotes")
                                                    .description("리뷰 노트")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/human-reject - Human 2차 거절")
    class HumanReject {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long feedbackQueueId = 1L;
            var request = RejectFeedbackApiRequestFixture.valid();
            var command =
                    ProcessFeedbackCommand.reject(
                            feedbackQueueId, FeedbackAction.HUMAN_REJECT, request.reviewNotes());
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "MEDIUM",
                            "{\"code\":\"AGG-001\"}",
                            "HUMAN_REJECTED",
                            request.reviewNotes(),
                            Instant.now(),
                            Instant.now());
            var response = FeedbackQueueApiResponseFixture.humanRejected();

            given(commandMapper.toHumanRejectCommand(any(), any())).willReturn(command);
            given(processFeedbackUseCase.execute(any())).willReturn(result);
            given(queryMapper.toResponse(any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            patch(FeedbackQueueApiEndpoints.HUMAN_REJECT, feedbackQueueId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("HUMAN_REJECTED"))
                    .andDo(
                            document(
                                    "feedback-queue-human-reject",
                                    pathParameters(
                                            parameterWithName("feedbackQueueId")
                                                    .description("FeedbackQueue ID")),
                                    requestFields(
                                            fieldWithPath("reviewNotes")
                                                    .description("거절 사유")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.feedbackQueueId")
                                                    .description("피드백 큐 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.targetType")
                                                    .description("대상 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.feedbackType")
                                                    .description("피드백 유형")
                                                    .type(String.class),
                                            fieldWithPath("data.riskLevel")
                                                    .description("리스크 레벨")
                                                    .type(String.class),
                                            fieldWithPath("data.payload")
                                                    .description("피드백 페이로드")
                                                    .type(String.class),
                                            fieldWithPath("data.status")
                                                    .description("현재 상태")
                                                    .type(String.class),
                                            fieldWithPath("data.reviewNotes")
                                                    .description("리뷰 노트 (거절 사유)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("POST /{feedbackQueueId}/merge - 피드백 머지")
    class Merge {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long feedbackQueueId = 1L;
            var command = new MergeFeedbackCommand(feedbackQueueId);
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "MERGED",
                            null,
                            Instant.now(),
                            Instant.now());
            var response = FeedbackQueueApiResponseFixture.merged();

            given(commandMapper.toMergeCommand(feedbackQueueId)).willReturn(command);
            given(mergeFeedbackUseCase.execute(any())).willReturn(result);
            given(queryMapper.toResponse(any())).willReturn(response);

            // When & Then
            mockMvc.perform(post(FeedbackQueueApiEndpoints.MERGE, feedbackQueueId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("MERGED"))
                    .andDo(
                            document(
                                    "feedback-queue-merge",
                                    pathParameters(
                                            parameterWithName("feedbackQueueId")
                                                    .description("FeedbackQueue ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.feedbackQueueId")
                                                    .description("피드백 큐 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.targetType")
                                                    .description("대상 타입")
                                                    .type(String.class),
                                            fieldWithPath("data.targetId")
                                                    .description("대상 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.feedbackType")
                                                    .description("피드백 유형")
                                                    .type(String.class),
                                            fieldWithPath("data.riskLevel")
                                                    .description("리스크 레벨")
                                                    .type(String.class),
                                            fieldWithPath("data.payload")
                                                    .description("피드백 페이로드")
                                                    .type(String.class),
                                            fieldWithPath("data.status")
                                                    .description("현재 상태")
                                                    .type(String.class),
                                            fieldWithPath("data.reviewNotes")
                                                    .description("리뷰 노트")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("data.createdAt")
                                                    .description("생성 일시")
                                                    .type(String.class),
                                            fieldWithPath("data.updatedAt")
                                                    .description("수정 일시")
                                                    .type(String.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }
}
