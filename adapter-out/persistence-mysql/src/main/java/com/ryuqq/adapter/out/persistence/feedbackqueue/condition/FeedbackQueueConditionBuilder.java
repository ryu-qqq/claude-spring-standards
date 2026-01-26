package com.ryuqq.adapter.out.persistence.feedbackqueue.condition;

import static com.ryuqq.adapter.out.persistence.feedbackqueue.entity.QFeedbackQueueJpaEntity.feedbackQueueJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueConditionBuilder - 피드백 큐 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>Note: FeedbackQueue 도메인은 soft delete를 사용하지 않습니다.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueConditionBuilder {

    /**
     * 상태 일치 조건
     *
     * @param status 피드백 상태
     * @return status 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression statusEq(FeedbackStatus status) {
        return status != null ? feedbackQueueJpaEntity.status.eq(status) : null;
    }

    public BooleanExpression statusesIn(List<FeedbackStatus> statuses) {
        return statuses != null && !statuses.isEmpty()
                ? feedbackQueueJpaEntity.status.in(statuses)
                : null;
    }

    /**
     * 대상 타입 일치 조건
     *
     * @param targetType 피드백 대상 타입
     * @return targetType 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression targetTypeEq(FeedbackTargetType targetType) {
        return targetType != null ? feedbackQueueJpaEntity.targetType.eq(targetType) : null;
    }

    public BooleanExpression targetTypesIn(List<FeedbackTargetType> targetTypes) {
        return targetTypes != null && !targetTypes.isEmpty()
                ? feedbackQueueJpaEntity.targetType.in(targetTypes)
                : null;
    }

    public BooleanExpression feedbackTypesIn(List<FeedbackType> feedbackTypes) {
        return feedbackTypes != null && !feedbackTypes.isEmpty()
                ? feedbackQueueJpaEntity.feedbackType.in(feedbackTypes)
                : null;
    }

    /**
     * 리스크 수준 일치 조건
     *
     * @param riskLevel 리스크 수준
     * @return riskLevel 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression riskLevelEq(RiskLevel riskLevel) {
        return riskLevel != null ? feedbackQueueJpaEntity.riskLevel.eq(riskLevel) : null;
    }

    public BooleanExpression riskLevelsIn(List<RiskLevel> riskLevels) {
        return riskLevels != null && !riskLevels.isEmpty()
                ? feedbackQueueJpaEntity.riskLevel.in(riskLevels)
                : null;
    }

    /**
     * 처리 액션 필터
     *
     * <p>FeedbackAction은 저장 필드가 아니므로 현재 상태/리스크로부터 “가능한 액션”을 유도하여 필터링합니다.
     *
     * <ul>
     *   <li>LLM_APPROVE / LLM_REJECT: status == PENDING
     *   <li>HUMAN_APPROVE / HUMAN_REJECT: status == LLM_APPROVED AND riskLevel == MEDIUM
     * </ul>
     *
     * @param actions 처리 액션 목록
     * @return 액션 필터 조건 (nullable이면 null 반환)
     */
    public BooleanExpression actionsIn(List<FeedbackAction> actions) {
        if (actions == null || actions.isEmpty()) {
            return null;
        }

        BooleanExpression merged = null;
        for (FeedbackAction action : actions) {
            if (action == null) {
                continue;
            }
            BooleanExpression expr =
                    switch (action) {
                        case LLM_APPROVE, LLM_REJECT ->
                                feedbackQueueJpaEntity.status.eq(FeedbackStatus.PENDING);
                        case HUMAN_APPROVE, HUMAN_REJECT ->
                                feedbackQueueJpaEntity
                                        .status
                                        .eq(FeedbackStatus.LLM_APPROVED)
                                        .and(feedbackQueueJpaEntity.riskLevel.eq(RiskLevel.MEDIUM));
                    };
            merged = merged == null ? expr : merged.or(expr);
        }
        return merged;
    }

    /**
     * 대상 ID 일치 조건
     *
     * @param targetId 피드백 대상 ID
     * @return targetId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression targetIdEq(Long targetId) {
        return targetId != null ? feedbackQueueJpaEntity.targetId.eq(targetId) : null;
    }

    /**
     * 커서 기반 페이징 조건
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? feedbackQueueJpaEntity.id.lt(cursor) : null;
    }
}
