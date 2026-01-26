package com.ryuqq.adapter.out.persistence.codingrule.repository;

import static com.ryuqq.adapter.out.persistence.codingrule.entity.QCodingRuleJpaEntity.codingRuleJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.codingrule.condition.CodingRuleConditionBuilder;
import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.CodingRuleIndexData;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * CodingRuleQueryDslRepository - 코딩 규칙 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class CodingRuleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final CodingRuleConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public CodingRuleQueryDslRepository(
            JPAQueryFactory queryFactory, CodingRuleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 검색 조건으로 코딩 규칙 조회
     *
     * @param conventionId 컨벤션 ID (nullable)
     * @param category 카테고리 (nullable)
     * @param severity 심각도 (nullable)
     * @return 검색 결과
     */
    public List<CodingRuleJpaEntity> search(
            Long conventionId, RuleCategory category, RuleSeverity severity) {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.conventionIdEq(conventionId),
                        conditionBuilder.categoryEq(category),
                        conditionBuilder.severityEq(severity))
                .orderBy(codingRuleJpaEntity.code.asc())
                .fetch();
    }

    /**
     * Convention ID로 규칙 목록 조회
     *
     * @param conventionId 컨벤션 ID
     * @return 규칙 목록
     */
    public List<CodingRuleJpaEntity> findByConventionId(Long conventionId) {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        codingRuleJpaEntity.conventionId.eq(conventionId),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(codingRuleJpaEntity.code.asc())
                .fetch();
    }

    /**
     * Zero-Tolerance 규칙만 조회
     *
     * <p>ZeroToleranceRule 테이블에 연결된 CodingRule을 조회합니다.
     *
     * @return Zero-Tolerance 규칙 목록
     */
    public List<CodingRuleJpaEntity> findZeroToleranceRules() {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(conditionBuilder.hasZeroToleranceRule(), conditionBuilder.deletedAtIsNull())
                .orderBy(codingRuleJpaEntity.code.asc())
                .fetch();
    }

    /**
     * ID로 규칙 조회
     *
     * @param id 규칙 ID
     * @return 규칙 Optional
     */
    public Optional<CodingRuleJpaEntity> findById(Long id) {
        CodingRuleJpaEntity entity =
                queryFactory
                        .selectFrom(codingRuleJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id 규칙 ID
     * @return 존재하면 true
     */
    public boolean existsById(Long id) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(codingRuleJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 규칙 코드로 조회
     *
     * @param code 규칙 코드
     * @return 규칙 Optional
     */
    public Optional<CodingRuleJpaEntity> findByCode(String code) {
        CodingRuleJpaEntity entity =
                queryFactory
                        .selectFrom(codingRuleJpaEntity)
                        .where(conditionBuilder.codeEq(code), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 커서 기반 슬라이스 조회 (레거시 - 호환성 유지)
     *
     * @param conventionId 컨벤션 ID (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 규칙 목록
     */
    public List<CodingRuleJpaEntity> findBySlice(Long conventionId, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.conventionIdEq(conventionId),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(codingRuleJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 슬라이스 조건으로 CodingRule 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return CodingRule 엔티티 목록
     */
    public List<CodingRuleJpaEntity> findBySliceCriteria(CodingRuleSliceCriteria criteria) {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.categoriesIn(criteria),
                        conditionBuilder.severitiesIn(criteria),
                        conditionBuilder.searchByField(criteria),
                        conditionBuilder.cursorLt(criteria))
                .orderBy(codingRuleJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @return 존재하면 true
     */
    public boolean existsByConventionIdAndCode(Long conventionId, String code) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(codingRuleJpaEntity)
                        .where(
                                codingRuleJpaEntity.conventionId.eq(conventionId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 컨벤션 내 규칙 코드 존재 여부 확인 (특정 ID 제외)
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @param excludeId 제외할 규칙 ID
     * @return 존재하면 true
     */
    public boolean existsByConventionIdAndCodeExcluding(
            Long conventionId, String code, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(codingRuleJpaEntity)
                        .where(
                                codingRuleJpaEntity.conventionId.eq(conventionId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 키워드 검색
     *
     * <p>code, name, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param conventionId 컨벤션 ID (nullable)
     * @return 검색된 코딩 규칙 목록
     */
    public List<CodingRuleJpaEntity> searchByKeyword(String keyword, Long conventionId) {
        return queryFactory
                .selectFrom(codingRuleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.conventionIdEq(conventionId),
                        conditionBuilder.keywordContains(keyword))
                .orderBy(codingRuleJpaEntity.code.asc())
                .fetch();
    }

    /**
     * 규칙 인덱스 조회 (DTO Projection)
     *
     * <p>code, name, severity, category만 조회하여 캐싱 효율성을 높입니다.
     *
     * <p>QDSL-003: DTO Projection 사용 (성능 최적화).
     *
     * @param criteria 인덱스 조회 조건
     * @return 규칙 인덱스 데이터 목록
     */
    public List<CodingRuleIndexData> findRuleIndex(CodingRuleIndexCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CodingRuleIndexData.class,
                                codingRuleJpaEntity.code,
                                codingRuleJpaEntity.name,
                                codingRuleJpaEntity.severity.stringValue(),
                                codingRuleJpaEntity.category.stringValue()))
                .from(codingRuleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.conventionIdEq(criteria.conventionId()),
                        conditionBuilder.severitiesIn(criteria),
                        conditionBuilder.categoriesIn(criteria))
                .orderBy(codingRuleJpaEntity.code.asc())
                .fetch();
    }
}
