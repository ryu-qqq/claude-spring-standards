package com.ryuqq.application.feedbackqueue.dto.command;

/**
 * MergeFeedbackCommand - 피드백 머지 커맨드
 *
 * <p>승인된 피드백을 실제 대상 테이블에 반영할 때 사용합니다. Safe 레벨은 LLM_APPROVED 후 자동 머지, Medium 레벨은 HUMAN_APPROVED 후
 * 머지됩니다.
 *
 * @param feedbackId 피드백 ID
 * @author ryu-qqq
 */
public record MergeFeedbackCommand(Long feedbackId) {}
