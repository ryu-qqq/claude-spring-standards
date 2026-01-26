package com.ryuqq.application.feedbackqueue.port.in;

import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;

/**
 * ProcessFeedbackUseCase - 피드백 처리 통합 UseCase
 *
 * <p>LLM/Human 승인/거절 4가지 액션을 통합하여 처리합니다.
 *
 * <ul>
 *   <li>LLM_APPROVE: PENDING → LLM_APPROVED (UC-S03)
 *   <li>LLM_REJECT: PENDING → LLM_REJECTED (UC-S04)
 *   <li>HUMAN_APPROVE: LLM_APPROVED → HUMAN_APPROVED (UC-S05)
 *   <li>HUMAN_REJECT: LLM_APPROVED → HUMAN_REJECTED (UC-S06)
 * </ul>
 *
 * @author ryu-qqq
 */
public interface ProcessFeedbackUseCase {

    /**
     * 피드백 처리
     *
     * @param command 피드백 처리 커맨드 (액션, 거절 사유 포함)
     * @return 처리된 피드백 정보
     */
    FeedbackQueueResult execute(ProcessFeedbackCommand command);
}
