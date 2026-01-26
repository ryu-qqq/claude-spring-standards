package com.ryuqq.application.feedbackqueue.fixture;

import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;

/**
 * ProcessFeedbackCommand Test Fixture
 *
 * @author development-team
 */
public final class ProcessFeedbackCommandFixture {

    private static final Long DEFAULT_FEEDBACK_ID = 1L;

    private ProcessFeedbackCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 기본 ProcessFeedbackCommand 생성 (LLM_APPROVE)
     *
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand defaultCommand() {
        return llmApproveCommand(DEFAULT_FEEDBACK_ID);
    }

    /**
     * LLM_APPROVE 액션 커맨드 생성
     *
     * @param feedbackId 피드백 ID
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand llmApproveCommand(Long feedbackId) {
        return ProcessFeedbackCommand.approve(feedbackId, FeedbackAction.LLM_APPROVE);
    }

    /**
     * LLM_REJECT 액션 커맨드 생성
     *
     * @param feedbackId 피드백 ID
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand llmRejectCommand(Long feedbackId) {
        return ProcessFeedbackCommand.reject(
                feedbackId, FeedbackAction.LLM_REJECT, "LLM rejected: Invalid format");
    }

    /**
     * LLM_REJECT 액션 커맨드 생성 (사유 지정)
     *
     * @param feedbackId 피드백 ID
     * @param reviewNotes 거절 사유
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand llmRejectCommand(Long feedbackId, String reviewNotes) {
        return ProcessFeedbackCommand.reject(feedbackId, FeedbackAction.LLM_REJECT, reviewNotes);
    }

    /**
     * HUMAN_APPROVE 액션 커맨드 생성
     *
     * @param feedbackId 피드백 ID
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand humanApproveCommand(Long feedbackId) {
        return ProcessFeedbackCommand.approve(feedbackId, FeedbackAction.HUMAN_APPROVE);
    }

    /**
     * HUMAN_REJECT 액션 커맨드 생성
     *
     * @param feedbackId 피드백 ID
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand humanRejectCommand(Long feedbackId) {
        return ProcessFeedbackCommand.reject(
                feedbackId, FeedbackAction.HUMAN_REJECT, "Human rejected: Security concern");
    }

    /**
     * HUMAN_REJECT 액션 커맨드 생성 (사유 지정)
     *
     * @param feedbackId 피드백 ID
     * @param reviewNotes 거절 사유
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand humanRejectCommand(Long feedbackId, String reviewNotes) {
        return ProcessFeedbackCommand.reject(feedbackId, FeedbackAction.HUMAN_REJECT, reviewNotes);
    }

    /**
     * 특정 액션과 리뷰 노트로 커맨드 생성
     *
     * @param feedbackId 피드백 ID
     * @param action 액션
     * @param reviewNotes 리뷰 노트 (nullable)
     * @return ProcessFeedbackCommand
     */
    public static ProcessFeedbackCommand withParams(
            Long feedbackId, FeedbackAction action, String reviewNotes) {
        return new ProcessFeedbackCommand(feedbackId, action, reviewNotes);
    }

    /**
     * 모든 액션 타입별 커맨드 배열 생성
     *
     * @param feedbackId 피드백 ID
     * @return 4개 액션 타입별 커맨드 배열
     */
    public static ProcessFeedbackCommand[] allActionCommands(Long feedbackId) {
        return new ProcessFeedbackCommand[] {
            llmApproveCommand(feedbackId),
            llmRejectCommand(feedbackId),
            humanApproveCommand(feedbackId),
            humanRejectCommand(feedbackId)
        };
    }
}
