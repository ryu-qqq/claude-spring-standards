package com.ryuqq.adapter.out.persistence.zerotolerance.condition;

import static com.ryuqq.adapter.out.persistence.codingrule.entity.QCodingRuleJpaEntity.codingRuleJpaEntity;
import static com.ryuqq.adapter.out.persistence.zerotolerance.entity.QZeroToleranceRuleJpaEntity.zeroToleranceRuleJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceRuleSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleConditionBuilder - Zero-Tolerance 규칙 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>Long FK 전략을 사용하며, JPA 관계 어노테이션 없이 서브쿼리로 연관 데이터를 확인합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleConditionBuilder {

    /**
     * ZeroToleranceRule 테이블에 연결된 CodingRule인지 확인하는 EXISTS 서브쿼리
     *
     * <p>Long FK 전략을 사용하여 zeroToleranceRule.ruleId가 codingRule.id와 일치하는지 확인합니다.
     *
     * @return EXISTS 서브쿼리 조건
     */
    public BooleanExpression hasZeroToleranceRule() {
        return JPAExpressions.selectOne()
                .from(zeroToleranceRuleJpaEntity)
                .where(
                        zeroToleranceRuleJpaEntity.ruleId.eq(codingRuleJpaEntity.id),
                        zeroToleranceRuleJpaEntity.deletedAt.isNull())
                .exists();
    }

    /**
     * CodingRule 삭제되지 않은 레코드 조건
     *
     * @return codingRuleJpaEntity.deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return codingRuleJpaEntity.deletedAt.isNull();
    }

    /**
     * 컨벤션 ID 일치 조건
     *
     * @param conventionId 컨벤션 ID
     * @return conventionId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression conventionIdEq(Long conventionId) {
        return conventionId != null ? codingRuleJpaEntity.conventionId.eq(conventionId) : null;
    }

    /**
     * 컨벤션 ID 목록 IN 조건
     *
     * @param conventionIds 컨벤션 ID 목록
     * @return conventionId IN 조건 (nullable이면 null 반환)
     */
    public BooleanExpression conventionIdsIn(List<Long> conventionIds) {
        return conventionIds != null && !conventionIds.isEmpty()
                ? codingRuleJpaEntity.conventionId.in(conventionIds)
                : null;
    }

    /**
     * 탐지 방식 목록 IN 조건 (ZeroToleranceRule 테이블)
     *
     * @param detectionTypes 탐지 방식 목록
     * @return detectionType IN 조건 (nullable이면 null 반환)
     */
    public BooleanExpression detectionTypesIn(List<String> detectionTypes) {
        if (detectionTypes == null || detectionTypes.isEmpty()) {
            return null;
        }
        return JPAExpressions.selectOne()
                .from(zeroToleranceRuleJpaEntity)
                .where(
                        zeroToleranceRuleJpaEntity.ruleId.eq(codingRuleJpaEntity.id),
                        zeroToleranceRuleJpaEntity.detectionType.in(detectionTypes),
                        zeroToleranceRuleJpaEntity.deletedAt.isNull())
                .exists();
    }

    /**
     * 검색 조건 (ZeroToleranceRule 테이블)
     *
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return 검색 조건 (nullable이면 null 반환)
     */
    public BooleanExpression searchContains(
            ZeroToleranceRuleSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }
        return switch (searchField) {
            case TYPE ->
                    JPAExpressions.selectOne()
                            .from(zeroToleranceRuleJpaEntity)
                            .where(
                                    zeroToleranceRuleJpaEntity.ruleId.eq(codingRuleJpaEntity.id),
                                    zeroToleranceRuleJpaEntity.type.containsIgnoreCase(searchWord),
                                    zeroToleranceRuleJpaEntity.deletedAt.isNull())
                            .exists();
        };
    }

    /**
     * PR 자동 거부 여부 조건 (ZeroToleranceRule 테이블)
     *
     * @param autoRejectPr PR 자동 거부 여부
     * @return autoRejectPr 조건 (nullable이면 null 반환)
     */
    public BooleanExpression autoRejectPrEq(Boolean autoRejectPr) {
        if (autoRejectPr == null) {
            return null;
        }
        return JPAExpressions.selectOne()
                .from(zeroToleranceRuleJpaEntity)
                .where(
                        zeroToleranceRuleJpaEntity.ruleId.eq(codingRuleJpaEntity.id),
                        zeroToleranceRuleJpaEntity.autoRejectPr.eq(autoRejectPr),
                        zeroToleranceRuleJpaEntity.deletedAt.isNull())
                .exists();
    }

    /**
     * 커서 기반 페이징 조건
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? codingRuleJpaEntity.id.lt(cursor) : null;
    }

    /**
     * CodingRule ID 일치 조건
     *
     * @param ruleId 코딩 규칙 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression ruleIdEq(Long ruleId) {
        return ruleId != null ? codingRuleJpaEntity.id.eq(ruleId) : null;
    }
}
