package com.ryuqq.adapter.out.persistence.checklistitem.repository;

import static com.ryuqq.adapter.out.persistence.checklistitem.entity.QChecklistItemJpaEntity.checklistItemJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.checklistitem.condition.ChecklistItemConditionBuilder;
import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ChecklistItemQueryDslRepository - 체크리스트 항목 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ChecklistItemQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ChecklistItemConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ChecklistItemQueryDslRepository(
            JPAQueryFactory queryFactory, ChecklistItemConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 체크리스트 항목 조회
     *
     * @param id 체크리스트 항목 ID
     * @return 체크리스트 항목 Optional
     */
    public Optional<ChecklistItemJpaEntity> findById(Long id) {
        ChecklistItemJpaEntity entity =
                queryFactory
                        .selectFrom(checklistItemJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 코딩 규칙 ID로 체크리스트 항목 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 체크리스트 항목 목록
     */
    public List<ChecklistItemJpaEntity> findByRuleId(Long ruleId) {
        return queryFactory
                .selectFrom(checklistItemJpaEntity)
                .where(checklistItemJpaEntity.ruleId.eq(ruleId), conditionBuilder.deletedAtIsNull())
                .orderBy(checklistItemJpaEntity.sequenceOrder.asc())
                .fetch();
    }

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param ruleId 코딩 규칙 ID (nullable)
     * @param checkType 체크 타입 문자열 (nullable)
     * @param automationTool 자동화 도구 문자열 (nullable)
     * @param isCritical 필수 여부 (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 체크리스트 항목 목록
     */
    public List<ChecklistItemJpaEntity> findBySlice(
            Long ruleId,
            String checkType,
            String automationTool,
            Boolean isCritical,
            Long cursor,
            int fetchSize) {
        return queryFactory
                .selectFrom(checklistItemJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.ruleIdEq(ruleId),
                        conditionBuilder.checkTypeEq(checkType),
                        conditionBuilder.automationToolEq(automationTool),
                        conditionBuilder.isCriticalEq(isCritical),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(checklistItemJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 전체 체크리스트 항목 목록 조회
     *
     * @return 체크리스트 항목 목록
     */
    public List<ChecklistItemJpaEntity> searchAll() {
        return queryFactory
                .selectFrom(checklistItemJpaEntity)
                .where(conditionBuilder.deletedAtIsNull())
                .orderBy(checklistItemJpaEntity.id.asc())
                .fetch();
    }

    /**
     * ChecklistItemSliceCriteria 기반 슬라이스 조회
     *
     * <p>복합 조건(코딩 규칙 ID, 체크 타입, 자동화 도구) 필터링과 커서 기반 페이징을 지원합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 체크리스트 항목 목록
     */
    public List<ChecklistItemJpaEntity> findBySliceCriteria(ChecklistItemSliceCriteria criteria) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(conditionBuilder.deletedAtIsNull());
        where.and(conditionBuilder.ruleIdsIn(criteria));
        where.and(conditionBuilder.checkTypesIn(criteria));
        where.and(conditionBuilder.automationToolsIn(criteria));
        where.and(conditionBuilder.isCriticalEq(criteria.isCritical()));
        where.and(conditionBuilder.cursorLt(criteria));

        return queryFactory
                .selectFrom(checklistItemJpaEntity)
                .where(where)
                .orderBy(checklistItemJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 코딩 규칙 ID와 순서로 존재 여부 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @return 존재하면 true
     */
    public boolean existsByRuleIdAndSequenceOrder(Long ruleId, int sequenceOrder) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(checklistItemJpaEntity)
                        .where(
                                checklistItemJpaEntity.ruleId.eq(ruleId),
                                conditionBuilder.sequenceOrderEq(sequenceOrder),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return count != null;
    }
}
