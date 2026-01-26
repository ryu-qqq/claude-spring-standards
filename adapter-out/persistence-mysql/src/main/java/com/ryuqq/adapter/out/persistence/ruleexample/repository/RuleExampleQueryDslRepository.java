package com.ryuqq.adapter.out.persistence.ruleexample.repository;

import static com.ryuqq.adapter.out.persistence.ruleexample.entity.QRuleExampleJpaEntity.ruleExampleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.ruleexample.condition.RuleExampleConditionBuilder;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * RuleExampleQueryDslRepository - 규칙 예시 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class RuleExampleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final RuleExampleConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public RuleExampleQueryDslRepository(
            JPAQueryFactory queryFactory, RuleExampleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 규칙 예시 조회
     *
     * @param id 규칙 예시 ID
     * @return 규칙 예시 Optional
     */
    public Optional<RuleExampleJpaEntity> findById(Long id) {
        RuleExampleJpaEntity entity =
                queryFactory
                        .selectFrom(ruleExampleJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 코딩 규칙 ID로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 규칙 예시 목록
     */
    public List<RuleExampleJpaEntity> findByRuleId(Long ruleId) {
        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(ruleExampleJpaEntity.ruleId.eq(ruleId), conditionBuilder.deletedAtIsNull())
                .orderBy(ruleExampleJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 코딩 규칙 ID와 예시 타입으로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @param exampleType 예시 타입 문자열
     * @return 규칙 예시 목록
     */
    public List<RuleExampleJpaEntity> findByRuleIdAndType(Long ruleId, String exampleType) {
        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(
                        ruleExampleJpaEntity.ruleId.eq(ruleId),
                        conditionBuilder.exampleTypeEq(exampleType),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(ruleExampleJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param ruleId 코딩 규칙 ID (nullable)
     * @param exampleType 예시 타입 문자열 (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 규칙 예시 목록
     */
    public List<RuleExampleJpaEntity> findBySlice(
            Long ruleId, String exampleType, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.ruleIdEq(ruleId),
                        conditionBuilder.exampleTypeEq(exampleType),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(ruleExampleJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 전체 규칙 예시 목록 조회
     *
     * @return 규칙 예시 목록
     */
    public List<RuleExampleJpaEntity> searchAll() {
        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(conditionBuilder.deletedAtIsNull())
                .orderBy(ruleExampleJpaEntity.id.asc())
                .fetch();
    }

    /**
     * RuleExampleSliceCriteria 기반 슬라이스 조회
     *
     * <p>복합 조건(코딩 규칙 ID, 예시 타입, 언어) 필터링과 커서 기반 페이징을 지원합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 규칙 예시 목록
     */
    public List<RuleExampleJpaEntity> findBySliceCriteria(RuleExampleSliceCriteria criteria) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(conditionBuilder.deletedAtIsNull());
        where.and(conditionBuilder.ruleIdsIn(criteria));
        where.and(conditionBuilder.exampleTypesIn(criteria));
        where.and(conditionBuilder.languagesIn(criteria));
        where.and(conditionBuilder.cursorLt(criteria));

        return queryFactory
                .selectFrom(ruleExampleJpaEntity)
                .where(where)
                .orderBy(ruleExampleJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }
}
