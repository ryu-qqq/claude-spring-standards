package com.ryuqq.adapter.out.persistence.zerotolerance.repository;

import static com.ryuqq.adapter.out.persistence.checklistitem.entity.QChecklistItemJpaEntity.checklistItemJpaEntity;
import static com.ryuqq.adapter.out.persistence.codingrule.entity.QCodingRuleJpaEntity.codingRuleJpaEntity;
import static com.ryuqq.adapter.out.persistence.ruleexample.entity.QRuleExampleJpaEntity.ruleExampleJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.zerotolerance.condition.ZeroToleranceRuleConditionBuilder;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ZeroToleranceRuleQueryDslRepository - Zero-Tolerance 규칙 상세 조회 QueryDSL 레포지토리
 *
 * <p>ZeroToleranceRule 테이블에 연결된 CodingRule과 관련 RuleExample, ChecklistItem을 조회합니다.
 *
 * <p>Long FK 전략을 사용하며, JPA 관계 어노테이션 없이 별도 쿼리로 연관 데이터를 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class ZeroToleranceRuleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ZeroToleranceRuleConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ZeroToleranceRuleQueryDslRepository(
            JPAQueryFactory queryFactory, ZeroToleranceRuleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 Zero-Tolerance CodingRule 단건 조회
     *
     * <p>ZeroToleranceRule 테이블에 연결된 CodingRule을 조회합니다.
     *
     * <p>deleted_at IS NULL인 CodingRule만 조회합니다.
     *
     * @param ruleId 코딩 규칙 ID
     * @return CodingRuleJpaEntity Optional
     */
    public Optional<CodingRuleJpaEntity> findZeroToleranceRuleById(Long ruleId) {
        CodingRuleJpaEntity entity =
                queryFactory
                        .selectFrom(codingRuleJpaEntity)
                        .where(
                                conditionBuilder.ruleIdEq(ruleId),
                                conditionBuilder.hasZeroToleranceRule(),
                                conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 규칙 ID로 관련 RuleExample 목록 조회
     *
     * <p>deleted_at IS NULL인 RuleExample만 조회합니다.
     *
     * @param ruleId 코딩 규칙 ID
     * @return RuleExampleJpaEntity 목록
     */
    public List<RuleExampleJpaEntity> findRuleExamplesByRuleId(Long ruleId) {
        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(
                        ruleExampleJpaEntity.ruleId.eq(ruleId),
                        ruleExampleJpaEntity.deletedAt.isNull())
                .orderBy(ruleExampleJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 규칙 ID로 관련 ChecklistItem 목록 조회
     *
     * <p>deleted_at IS NULL인 ChecklistItem만 조회합니다.
     *
     * @param ruleId 코딩 규칙 ID
     * @return ChecklistItemJpaEntity 목록
     */
    public List<ChecklistItemJpaEntity> findChecklistItemsByRuleId(Long ruleId) {
        return queryFactory
                .selectFrom(checklistItemJpaEntity)
                .where(
                        checklistItemJpaEntity.ruleId.eq(ruleId),
                        checklistItemJpaEntity.deletedAt.isNull())
                .orderBy(checklistItemJpaEntity.sequenceOrder.asc())
                .fetch();
    }

    /**
     * 커서 기반 슬라이스로 Zero-Tolerance CodingRule 목록 조회
     *
     * <p>ZeroToleranceRule 테이블에 연결되고 deleted_at IS NULL인 CodingRule을 조회합니다.
     *
     * @param conventionId 컨벤션 ID (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return CodingRuleJpaEntity 목록
     */
    public List<CodingRuleJpaEntity> findZeroToleranceRulesBySlice(
            Long conventionId, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        conditionBuilder.hasZeroToleranceRule(),
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.conventionIdEq(conventionId),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(codingRuleJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 슬라이스 조건으로 Zero-Tolerance CodingRule 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return CodingRuleJpaEntity 목록
     */
    public List<CodingRuleJpaEntity> findBySliceCriteria(
            com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria criteria) {
        List<Long> conventionIds =
                criteria.hasConventionFilter()
                        ? criteria.conventionIds().stream().map(ConventionId::value).toList()
                        : null;

        List<String> detectionTypes =
                criteria.hasDetectionTypeFilter()
                        ? criteria.detectionTypes().stream().map(Enum::name).toList()
                        : null;

        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        conditionBuilder.hasZeroToleranceRule(),
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.conventionIdsIn(conventionIds),
                        conditionBuilder.detectionTypesIn(detectionTypes),
                        conditionBuilder.searchContains(
                                criteria.searchField(), criteria.searchWord()),
                        conditionBuilder.autoRejectPrEq(criteria.autoRejectPr()),
                        conditionBuilder.cursorLt(criteria.cursorPageRequest().cursor()))
                .orderBy(codingRuleJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 여러 규칙 ID에 대한 RuleExample 목록 일괄 조회
     *
     * <p>N+1 문제 방지를 위해 IN 절로 일괄 조회합니다.
     *
     * @param ruleIds 코딩 규칙 ID 목록
     * @return RuleExampleJpaEntity 목록
     */
    public List<RuleExampleJpaEntity> findRuleExamplesByRuleIds(List<Long> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(
                        ruleExampleJpaEntity.ruleId.in(ruleIds),
                        ruleExampleJpaEntity.deletedAt.isNull())
                .orderBy(ruleExampleJpaEntity.ruleId.asc(), ruleExampleJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 여러 규칙 ID에 대한 ChecklistItem 목록 일괄 조회
     *
     * <p>N+1 문제 방지를 위해 IN 절로 일괄 조회합니다.
     *
     * @param ruleIds 코딩 규칙 ID 목록
     * @return ChecklistItemJpaEntity 목록
     */
    public List<ChecklistItemJpaEntity> findChecklistItemsByRuleIds(List<Long> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(checklistItemJpaEntity)
                .where(
                        checklistItemJpaEntity.ruleId.in(ruleIds),
                        checklistItemJpaEntity.deletedAt.isNull())
                .orderBy(
                        checklistItemJpaEntity.ruleId.asc(),
                        checklistItemJpaEntity.sequenceOrder.asc())
                .fetch();
    }
}
