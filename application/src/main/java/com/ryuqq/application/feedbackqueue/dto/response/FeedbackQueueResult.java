package com.ryuqq.application.feedbackqueue.dto.response;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import java.time.Instant;

/**
 * FeedbackQueueResult - 피드백 큐 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 피드백 ID
 * @param targetType 대상 타입
 * @param targetId 대상 ID (nullable)
 * @param feedbackType 피드백 유형
 * @param riskLevel 리스크 레벨
 * @param payload JSON 형태의 피드백 내용
 * @param status 현재 상태
 * @param reviewNotes 리뷰 노트 (거절 사유 등)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record FeedbackQueueResult(
        Long id,
        String targetType,
        Long targetId,
        String feedbackType,
        String riskLevel,
        String payload,
        String status,
        String reviewNotes,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용
     *
     * @param feedbackQueue FeedbackQueue 도메인 객체
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult from(FeedbackQueue feedbackQueue) {
        return new FeedbackQueueResult(
                feedbackQueue.idValue(),
                feedbackQueue.targetType().name(),
                feedbackQueue.targetId(),
                feedbackQueue.feedbackType().name(),
                feedbackQueue.riskLevel().name(),
                feedbackQueue.payloadValue(),
                feedbackQueue.status().name(),
                feedbackQueue.reviewNotesValue(),
                feedbackQueue.createdAt(),
                feedbackQueue.updatedAt());
    }
}
