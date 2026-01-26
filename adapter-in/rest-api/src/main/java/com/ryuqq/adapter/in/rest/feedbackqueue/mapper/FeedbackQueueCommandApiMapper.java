package com.ryuqq.adapter.in.rest.feedbackqueue.mapper;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.CreateFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.RejectFeedbackApiRequest;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueCommandApiMapper - FeedbackQueue Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-002: Mapper에서 Static 메서드 금지.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class FeedbackQueueCommandApiMapper {

    /**
     * CreateFeedbackApiRequest -> CreateFeedbackCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateFeedbackCommand toCommand(CreateFeedbackApiRequest request) {
        return new CreateFeedbackCommand(
                request.targetType(),
                request.targetId(),
                request.feedbackType(),
                request.payload());
    }

    /**
     * LLM 승인용 ProcessFeedbackCommand 생성
     *
     * @param feedbackQueueId FeedbackQueue ID (PathVariable)
     * @return ProcessFeedbackCommand (LLM_APPROVE 액션)
     */
    public ProcessFeedbackCommand toLlmApproveCommand(Long feedbackQueueId) {
        return ProcessFeedbackCommand.approve(feedbackQueueId, FeedbackAction.LLM_APPROVE);
    }

    /**
     * LLM 거절용 ProcessFeedbackCommand 생성
     *
     * @param feedbackQueueId FeedbackQueue ID (PathVariable)
     * @param request API 요청 DTO (거절 사유)
     * @return ProcessFeedbackCommand (LLM_REJECT 액션)
     */
    public ProcessFeedbackCommand toLlmRejectCommand(
            Long feedbackQueueId, RejectFeedbackApiRequest request) {
        String reviewNotes = request != null ? request.reviewNotes() : null;
        return ProcessFeedbackCommand.reject(
                feedbackQueueId, FeedbackAction.LLM_REJECT, reviewNotes);
    }

    /**
     * Human 승인용 ProcessFeedbackCommand 생성
     *
     * @param feedbackQueueId FeedbackQueue ID (PathVariable)
     * @return ProcessFeedbackCommand (HUMAN_APPROVE 액션)
     */
    public ProcessFeedbackCommand toHumanApproveCommand(Long feedbackQueueId) {
        return ProcessFeedbackCommand.approve(feedbackQueueId, FeedbackAction.HUMAN_APPROVE);
    }

    /**
     * Human 거절용 ProcessFeedbackCommand 생성
     *
     * @param feedbackQueueId FeedbackQueue ID (PathVariable)
     * @param request API 요청 DTO (거절 사유)
     * @return ProcessFeedbackCommand (HUMAN_REJECT 액션)
     */
    public ProcessFeedbackCommand toHumanRejectCommand(
            Long feedbackQueueId, RejectFeedbackApiRequest request) {
        String reviewNotes = request != null ? request.reviewNotes() : null;
        return ProcessFeedbackCommand.reject(
                feedbackQueueId, FeedbackAction.HUMAN_REJECT, reviewNotes);
    }

    /**
     * PathVariable ID -> MergeFeedbackCommand 변환
     *
     * @param feedbackQueueId FeedbackQueue ID (PathVariable)
     * @return Application Command DTO
     */
    public MergeFeedbackCommand toMergeCommand(Long feedbackQueueId) {
        return new MergeFeedbackCommand(feedbackQueueId);
    }
}
