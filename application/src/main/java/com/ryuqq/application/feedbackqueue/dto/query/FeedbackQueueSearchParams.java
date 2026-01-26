package com.ryuqq.application.feedbackqueue.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * FeedbackQueueSearchParams - FeedbackQueue 목록 조회 SearchParams DTO (커서 기반)
 *
 * <p>FeedbackQueue 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 상태/대상 타입/피드백 타입/리스크/액션 필터(복수)를 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param statuses 상태 필터 목록 (nullable)
 * @param targetTypes 대상 타입 필터 목록 (nullable)
 * @param feedbackTypes 피드백 타입 필터 목록 (nullable)
 * @param riskLevels 리스크 레벨 필터 목록 (nullable)
 * @param actions 처리 액션 필터 목록 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record FeedbackQueueSearchParams(
        CommonCursorParams cursorParams,
        List<String> statuses,
        List<String> targetTypes,
        List<String> feedbackTypes,
        List<String> riskLevels,
        List<String> actions) {

    public static FeedbackQueueSearchParams of(
            CommonCursorParams cursorParams,
            List<String> statuses,
            List<String> targetTypes,
            List<String> feedbackTypes,
            List<String> riskLevels,
            List<String> actions) {
        return new FeedbackQueueSearchParams(
                cursorParams, statuses, targetTypes, feedbackTypes, riskLevels, actions);
    }

    // Delegate Methods
    public String cursor() {
        return cursorParams.cursor();
    }

    public Integer size() {
        return cursorParams.size();
    }

    public boolean isFirstPage() {
        return cursorParams.isFirstPage();
    }

    public boolean hasCursor() {
        return cursorParams.hasCursor();
    }

    // Helper Methods
    public boolean hasStatuses() {
        return statuses != null && !statuses.isEmpty();
    }

    public boolean hasTargetTypes() {
        return targetTypes != null && !targetTypes.isEmpty();
    }

    public boolean hasFeedbackTypes() {
        return feedbackTypes != null && !feedbackTypes.isEmpty();
    }

    public boolean hasRiskLevels() {
        return riskLevels != null && !riskLevels.isEmpty();
    }

    public boolean hasActions() {
        return actions != null && !actions.isEmpty();
    }
}
