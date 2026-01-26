package com.ryuqq.application.feedbackqueue.dto.response;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import java.time.Instant;

/**
 * FeedbackQueueSummary - 피드백 큐 요약 정보
 *
 * <p>목록 조회 시 사용하는 간략한 피드백 정보입니다.
 *
 * @param id 피드백 ID
 * @param targetType 대상 타입
 * @param feedbackType 피드백 유형
 * @param riskLevel 리스크 레벨
 * @param status 현재 상태
 * @param createdAt 생성 시각
 * @author ryu-qqq
 */
public record FeedbackQueueSummary(
        Long id,
        String targetType,
        String feedbackType,
        String riskLevel,
        String status,
        Instant createdAt) {

    /**
     * Domain 객체로부터 Summary 생성
     *
     * @param feedbackQueue FeedbackQueue 도메인 객체
     * @return FeedbackQueueSummary
     */
    public static FeedbackQueueSummary from(FeedbackQueue feedbackQueue) {
        return new FeedbackQueueSummary(
                feedbackQueue.id().value(),
                feedbackQueue.targetType().name(),
                feedbackQueue.feedbackType().name(),
                feedbackQueue.riskLevel().name(),
                feedbackQueue.status().name(),
                feedbackQueue.createdAt());
    }
}
