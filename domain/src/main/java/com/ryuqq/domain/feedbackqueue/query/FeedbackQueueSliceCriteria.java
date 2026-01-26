package com.ryuqq.domain.feedbackqueue.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;

/**
 * FeedbackQueueSliceCriteria - FeedbackQueue 슬라이스 조회 조건 (커서 기반)
 *
 * <p>FeedbackQueue 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 FeedbackQueue ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param targetTypes 필터링할 대상 타입 목록 (optional)
 * @param statuses 필터링할 상태 목록 (optional)
 * @param feedbackTypes 필터링할 피드백 타입 목록 (optional)
 * @param riskLevels 필터링할 리스크 레벨 목록 (optional)
 * @param actions 필터링할 처리 액션 목록 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record FeedbackQueueSliceCriteria(
        List<FeedbackTargetType> targetTypes,
        List<FeedbackStatus> statuses,
        List<FeedbackType> feedbackTypes,
        List<RiskLevel> riskLevels,
        List<FeedbackAction> actions,
        CursorPageRequest<Long> cursorPageRequest) {

    public FeedbackQueueSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 조회)
     *
     * @param size 슬라이스 크기
     * @return FeedbackQueueSliceCriteria
     */
    public static FeedbackQueueSliceCriteria first(int size) {
        return new FeedbackQueueSliceCriteria(
                null, null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * 상태별 필터링된 슬라이스 조건 생성 (첫 페이지)
     *
     * @param status 상태
     * @param size 슬라이스 크기
     * @return FeedbackQueueSliceCriteria
     */
    public static FeedbackQueueSliceCriteria byStatus(FeedbackStatus status, int size) {
        return new FeedbackQueueSliceCriteria(
                null, List.of(status), null, null, null, CursorPageRequest.first(size));
    }

    /**
     * 대상 타입별 필터링된 슬라이스 조건 생성 (첫 페이지)
     *
     * @param targetType 대상 타입
     * @param size 슬라이스 크기
     * @return FeedbackQueueSliceCriteria
     */
    public static FeedbackQueueSliceCriteria byTargetType(FeedbackTargetType targetType, int size) {
        return new FeedbackQueueSliceCriteria(
                List.of(targetType), null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return FeedbackQueueSliceCriteria
     */
    public static FeedbackQueueSliceCriteria afterId(Long cursorId, int size) {
        return new FeedbackQueueSliceCriteria(
                null, null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 전체 조건으로 슬라이스 조건 생성
     *
     * @param targetType 대상 타입 (nullable)
     * @param status 상태 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return FeedbackQueueSliceCriteria
     */
    public static FeedbackQueueSliceCriteria of(
            List<FeedbackTargetType> targetTypes,
            List<FeedbackStatus> statuses,
            List<FeedbackType> feedbackTypes,
            List<RiskLevel> riskLevels,
            List<FeedbackAction> actions,
            CursorPageRequest<Long> cursorPageRequest) {
        return new FeedbackQueueSliceCriteria(
                targetTypes, statuses, feedbackTypes, riskLevels, actions, cursorPageRequest);
    }

    /**
     * 대상 타입 필터 존재 여부 확인
     *
     * @return targetType이 있으면 true
     */
    public boolean hasTargetTypeFilter() {
        return targetTypes != null && !targetTypes.isEmpty();
    }

    /**
     * 상태 필터 존재 여부 확인
     *
     * @return status가 있으면 true
     */
    public boolean hasStatusFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    public boolean hasFeedbackTypeFilter() {
        return feedbackTypes != null && !feedbackTypes.isEmpty();
    }

    public boolean hasRiskLevelFilter() {
        return riskLevels != null && !riskLevels.isEmpty();
    }

    public boolean hasActionFilter() {
        return actions != null && !actions.isEmpty();
    }

    /**
     * 첫 페이지 요청인지 확인
     *
     * @return cursor가 null이면 true
     */
    public boolean isFirstPage() {
        return cursorPageRequest.cursor() == null;
    }

    /**
     * 커서가 있는지 확인
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursorPageRequest.cursor() != null;
    }

    /**
     * 슬라이스 크기 반환 (편의 메서드)
     *
     * @return size
     */
    public int size() {
        return cursorPageRequest.size();
    }

    /**
     * 실제 조회 크기 반환 (hasNext 판단용 +1)
     *
     * @return size + 1
     */
    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }
}
