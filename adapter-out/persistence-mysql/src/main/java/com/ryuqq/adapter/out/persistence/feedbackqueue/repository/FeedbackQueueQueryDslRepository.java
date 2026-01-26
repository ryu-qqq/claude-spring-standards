package com.ryuqq.adapter.out.persistence.feedbackqueue.repository;

import static com.ryuqq.adapter.out.persistence.feedbackqueue.entity.QFeedbackQueueJpaEntity.feedbackQueueJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.feedbackqueue.condition.FeedbackQueueConditionBuilder;
import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * FeedbackQueueQueryDslRepository - 피드백 큐 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class FeedbackQueueQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final FeedbackQueueConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public FeedbackQueueQueryDslRepository(
            JPAQueryFactory queryFactory, FeedbackQueueConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 피드백 큐 조회
     *
     * @param id 피드백 큐 ID
     * @return 피드백 큐 Optional
     */
    public Optional<FeedbackQueueJpaEntity> findById(Long id) {
        FeedbackQueueJpaEntity entity =
                queryFactory
                        .selectFrom(feedbackQueueJpaEntity)
                        .where(feedbackQueueJpaEntity.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 상태별 피드백 큐 목록 조회
     *
     * @param status 처리 상태
     * @return 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findByStatus(FeedbackStatus status) {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(conditionBuilder.statusEq(status))
                .orderBy(feedbackQueueJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 대상 타입별 피드백 큐 목록 조회
     *
     * @param targetType 피드백 대상 타입
     * @return 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findByTargetType(FeedbackTargetType targetType) {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(conditionBuilder.targetTypeEq(targetType))
                .orderBy(feedbackQueueJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 리스크 수준별 피드백 큐 목록 조회
     *
     * @param riskLevel 리스크 수준
     * @return 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findByRiskLevel(RiskLevel riskLevel) {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(conditionBuilder.riskLevelEq(riskLevel))
                .orderBy(feedbackQueueJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 대기 중인 피드백 큐 목록 조회
     *
     * @return PENDING 상태의 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findPendingFeedbacks() {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(conditionBuilder.statusEq(FeedbackStatus.PENDING))
                .orderBy(feedbackQueueJpaEntity.createdAt.asc())
                .fetch();
    }

    /**
     * 자동 병합 가능한 피드백 큐 목록 조회
     *
     * <p>SAFE 리스크이고 LLM_APPROVED 상태인 피드백만 조회합니다.
     *
     * @return 자동 병합 가능한 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findAutoMergeableFeedbacks() {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(
                        conditionBuilder.statusEq(FeedbackStatus.LLM_APPROVED),
                        conditionBuilder.riskLevelEq(RiskLevel.SAFE))
                .orderBy(feedbackQueueJpaEntity.createdAt.asc())
                .fetch();
    }

    /**
     * 사람 승인 필요한 피드백 큐 목록 조회
     *
     * <p>MEDIUM 리스크이고 LLM_APPROVED 상태인 피드백만 조회합니다.
     *
     * @return 사람 승인 필요한 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findHumanReviewRequiredFeedbacks() {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(
                        conditionBuilder.statusEq(FeedbackStatus.LLM_APPROVED),
                        conditionBuilder.riskLevelEq(RiskLevel.MEDIUM))
                .orderBy(feedbackQueueJpaEntity.createdAt.asc())
                .fetch();
    }

    /**
     * 특정 대상에 대한 피드백 큐 목록 조회
     *
     * @param targetType 피드백 대상 타입
     * @param targetId 피드백 대상 ID
     * @return 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findByTarget(FeedbackTargetType targetType, Long targetId) {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(
                        conditionBuilder.targetTypeEq(targetType),
                        conditionBuilder.targetIdEq(targetId))
                .orderBy(feedbackQueueJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param status 처리 상태 (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findBySlice(
            FeedbackStatus status, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(conditionBuilder.statusEq(status), conditionBuilder.cursorLt(cursor))
                .orderBy(feedbackQueueJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 슬라이스 조건으로 피드백 큐 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 피드백 큐 목록
     */
    public List<FeedbackQueueJpaEntity> findBySliceCriteria(FeedbackQueueSliceCriteria criteria) {
        List<FeedbackStatus> statuses = criteria.hasStatusFilter() ? criteria.statuses() : null;
        List<FeedbackTargetType> targetTypes =
                criteria.hasTargetTypeFilter() ? criteria.targetTypes() : null;
        List<FeedbackType> feedbackTypes =
                criteria.hasFeedbackTypeFilter() ? criteria.feedbackTypes() : null;
        List<RiskLevel> riskLevels = criteria.hasRiskLevelFilter() ? criteria.riskLevels() : null;
        List<FeedbackAction> actions = criteria.hasActionFilter() ? criteria.actions() : null;

        return queryFactory
                .selectFrom(feedbackQueueJpaEntity)
                .where(
                        conditionBuilder.statusesIn(statuses),
                        conditionBuilder.targetTypesIn(targetTypes),
                        conditionBuilder.feedbackTypesIn(feedbackTypes),
                        conditionBuilder.riskLevelsIn(riskLevels),
                        conditionBuilder.actionsIn(actions),
                        conditionBuilder.cursorLt(criteria.cursorPageRequest().cursor()))
                .orderBy(feedbackQueueJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * ID로 피드백 큐 존재 여부 확인
     *
     * @param id 피드백 큐 ID
     * @return 존재하면 true
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(feedbackQueueJpaEntity)
                        .where(feedbackQueueJpaEntity.id.eq(id))
                        .fetchFirst();
        return result != null;
    }
}
