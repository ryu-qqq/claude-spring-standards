package com.ryuqq.application.feedbackqueue.dto.query;

/**
 * GetPendingFeedbacksQuery - PENDING 상태 피드백 조회 쿼리
 *
 * <p>LLM 검토 대기 중인 피드백 목록을 조회합니다.
 *
 * @param targetType 피드백 대상 타입 필터 (nullable)
 * @param cursorId 커서 ID (다음 페이지 조회 시)
 * @param size 페이지 크기
 * @author ryu-qqq
 */
public record GetPendingFeedbacksQuery(String targetType, Long cursorId, int size) {

    public GetPendingFeedbacksQuery {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
    }

    /**
     * 첫 페이지 조회용 팩토리 메서드
     *
     * @param size 페이지 크기
     * @return GetPendingFeedbacksQuery
     */
    public static GetPendingFeedbacksQuery firstPage(int size) {
        return new GetPendingFeedbacksQuery(null, null, size);
    }

    /**
     * 대상 타입으로 필터링된 첫 페이지 조회
     *
     * @param targetType 대상 타입
     * @param size 페이지 크기
     * @return GetPendingFeedbacksQuery
     */
    public static GetPendingFeedbacksQuery byTargetType(String targetType, int size) {
        return new GetPendingFeedbacksQuery(targetType, null, size);
    }

    /**
     * 커서 기반 다음 페이지 조회
     *
     * @param cursorId 커서 ID
     * @param size 페이지 크기
     * @return GetPendingFeedbacksQuery
     */
    public static GetPendingFeedbacksQuery afterCursor(Long cursorId, int size) {
        return new GetPendingFeedbacksQuery(null, cursorId, size);
    }

    /**
     * 첫 페이지 조회 여부 확인
     *
     * @return cursorId가 null이면 true
     */
    public boolean isFirstPage() {
        return cursorId == null;
    }

    /**
     * 대상 타입 필터가 있는지 확인
     *
     * @return targetType이 null이 아니면 true
     */
    public boolean hasTargetTypeFilter() {
        return targetType != null && !targetType.isBlank();
    }
}
