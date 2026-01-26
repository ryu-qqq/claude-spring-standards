package com.ryuqq.application.feedbackqueue.dto.command;

import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;

/**
 * ProcessFeedbackCommand - 피드백 처리 통합 커맨드
 *
 * <p>LLM/Human 승인/거절 4가지 액션을 통합하여 처리합니다.
 *
 * <p>CMD-001: Command는 record로 정의.
 *
 * @param feedbackId 피드백 ID
 * @param action 수행할 액션 (LLM_APPROVE, LLM_REJECT, HUMAN_APPROVE, HUMAN_REJECT)
 * @param reviewNotes 리뷰 노트 (거절 시 사유, 승인 시 null 가능)
 * @author ryu-qqq
 */
public record ProcessFeedbackCommand(Long feedbackId, FeedbackAction action, String reviewNotes) {

    /**
     * 승인 액션용 정적 팩토리 메서드
     *
     * @param feedbackId 피드백 ID
     * @param action 승인 액션 (LLM_APPROVE 또는 HUMAN_APPROVE)
     * @return 승인 커맨드
     */
    public static ProcessFeedbackCommand approve(Long feedbackId, FeedbackAction action) {
        return new ProcessFeedbackCommand(feedbackId, action, null);
    }

    /**
     * 거절 액션용 정적 팩토리 메서드
     *
     * @param feedbackId 피드백 ID
     * @param action 거절 액션 (LLM_REJECT 또는 HUMAN_REJECT)
     * @param reviewNotes 거절 사유
     * @return 거절 커맨드
     */
    public static ProcessFeedbackCommand reject(
            Long feedbackId, FeedbackAction action, String reviewNotes) {
        return new ProcessFeedbackCommand(feedbackId, action, reviewNotes);
    }
}
