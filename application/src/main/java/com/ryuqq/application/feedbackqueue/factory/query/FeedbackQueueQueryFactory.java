package com.ryuqq.application.feedbackqueue.factory.query;

import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.query.GetAwaitingHumanReviewQuery;
import com.ryuqq.application.feedbackqueue.dto.query.GetPendingFeedbacksQuery;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueQueryFactory - 피드백 큐 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueQueryFactory {

    /**
     * GetPendingFeedbacksQuery로부터 FeedbackQueueSliceCriteria 생성
     *
     * @param query 조회 쿼리
     * @return FeedbackQueueSliceCriteria
     */
    public FeedbackQueueSliceCriteria toSliceCriteria(GetPendingFeedbacksQuery query) {
        FeedbackTargetType targetType =
                query.hasTargetTypeFilter() ? FeedbackTargetType.valueOf(query.targetType()) : null;

        CursorPageRequest<Long> cursorPageRequest =
                query.cursorId() != null
                        ? CursorPageRequest.afterId(query.cursorId(), query.size())
                        : CursorPageRequest.first(query.size());

        return FeedbackQueueSliceCriteria.of(
                targetType != null ? List.of(targetType) : null,
                List.of(FeedbackStatus.PENDING),
                null,
                null,
                null,
                cursorPageRequest);
    }

    /**
     * GetAwaitingHumanReviewQuery로부터 FeedbackQueueSliceCriteria 생성
     *
     * @param query 조회 쿼리
     * @return FeedbackQueueSliceCriteria
     */
    public FeedbackQueueSliceCriteria toSliceCriteria(GetAwaitingHumanReviewQuery query) {
        FeedbackTargetType targetType =
                query.hasTargetTypeFilter() ? FeedbackTargetType.valueOf(query.targetType()) : null;

        CursorPageRequest<Long> cursorPageRequest =
                query.cursorId() != null
                        ? CursorPageRequest.afterId(query.cursorId(), query.size())
                        : CursorPageRequest.first(query.size());

        return FeedbackQueueSliceCriteria.of(
                targetType != null ? List.of(targetType) : null,
                List.of(FeedbackStatus.LLM_APPROVED),
                null,
                List.of(RiskLevel.MEDIUM),
                null,
                cursorPageRequest);
    }

    /**
     * FeedbackQueueSearchParams로부터 FeedbackQueueSliceCriteria 생성
     *
     * @param searchParams 검색 조건
     * @return FeedbackQueueSliceCriteria
     */
    public FeedbackQueueSliceCriteria createSliceCriteria(FeedbackQueueSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest =
                searchParams.isFirstPage()
                        ? CursorPageRequest.first(searchParams.size())
                        : CursorPageRequest.afterId(
                                Long.parseLong(searchParams.cursor()), searchParams.size());

        List<FeedbackStatus> statuses =
                searchParams.hasStatuses()
                        ? searchParams.statuses().stream().map(FeedbackStatus::valueOf).toList()
                        : null;
        List<FeedbackTargetType> targetTypes =
                searchParams.hasTargetTypes()
                        ? searchParams.targetTypes().stream()
                                .map(FeedbackTargetType::valueOf)
                                .toList()
                        : null;
        List<FeedbackType> feedbackTypes =
                searchParams.hasFeedbackTypes()
                        ? searchParams.feedbackTypes().stream().map(FeedbackType::valueOf).toList()
                        : null;
        List<RiskLevel> riskLevels =
                searchParams.hasRiskLevels()
                        ? searchParams.riskLevels().stream().map(RiskLevel::valueOf).toList()
                        : null;
        List<FeedbackAction> actions =
                searchParams.hasActions()
                        ? searchParams.actions().stream().map(FeedbackAction::valueOf).toList()
                        : null;

        return FeedbackQueueSliceCriteria.of(
                targetTypes, statuses, feedbackTypes, riskLevels, actions, cursorPageRequest);
    }

    /**
     * Long ID를 FeedbackQueueId로 변환
     *
     * @param feedbackId 피드백 ID
     * @return FeedbackQueueId
     */
    public FeedbackQueueId toFeedbackQueueId(Long feedbackId) {
        return FeedbackQueueId.of(feedbackId);
    }

    /**
     * String을 FeedbackTargetType으로 변환 (nullable)
     *
     * @param targetType 대상 타입 문자열
     * @return FeedbackTargetType (nullable)
     */
    public FeedbackTargetType toTargetType(String targetType) {
        if (targetType == null || targetType.isBlank()) {
            return null;
        }
        return FeedbackTargetType.valueOf(targetType);
    }

    /**
     * String을 FeedbackStatus로 변환 (nullable)
     *
     * @param status 상태 문자열
     * @return FeedbackStatus (nullable)
     */
    public FeedbackStatus toStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return FeedbackStatus.valueOf(status);
    }

    /**
     * String을 RiskLevel로 변환 (nullable)
     *
     * @param riskLevel 리스크 레벨 문자열
     * @return RiskLevel (nullable)
     */
    public RiskLevel toRiskLevel(String riskLevel) {
        if (riskLevel == null || riskLevel.isBlank()) {
            return null;
        }
        return RiskLevel.valueOf(riskLevel);
    }
}
