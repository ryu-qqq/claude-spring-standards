package com.ryuqq.application.feedbackqueue.dto.command;

/**
 * RejectFeedbackCommand - 피드백 거절 Command DTO
 *
 * <p>FeedbackQueue 거절 요청 정보를 담습니다.
 *
 * <p>DTO-001: Command DTO는 Record로 정의.
 *
 * @param feedbackQueueId 피드백 큐 ID
 * @param reviewNotes 거절 사유 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RejectFeedbackCommand(Long feedbackQueueId, String reviewNotes) {}
