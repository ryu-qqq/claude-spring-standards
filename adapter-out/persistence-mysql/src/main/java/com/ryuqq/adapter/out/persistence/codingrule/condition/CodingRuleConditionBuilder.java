package com.ryuqq.adapter.out.persistence.codingrule.condition;

import static com.ryuqq.adapter.out.persistence.codingrule.entity.QCodingRuleJpaEntity.codingRuleJpaEntity;
import static com.ryuqq.adapter.out.persistence.zerotolerance.entity.QZeroToleranceRuleJpaEntity.zeroToleranceRuleJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.CodingRuleSearchField;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import org.springframework.stereotype.Component;

/**
 * CodingRuleConditionBuilder - 코딩 규칙 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return codingRuleJpaEntity.deletedAt.isNull();
    }

    /**
     * ID 일치 조건
     *
     * @param id 규칙 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? codingRuleJpaEntity.id.eq(id) : null;
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
     * 컨벤션 ID 일치 조건
     *
     * @param conventionId 컨벤션 ID
     * @return conventionId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression conventionIdEq(Long conventionId) {
        return conventionId != null ? codingRuleJpaEntity.conventionId.eq(conventionId) : null;
    }

    /**
     * 카테고리 일치 조건
     *
     * @param category 규칙 카테고리
     * @return category 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression categoryEq(RuleCategory category) {
        return category != null ? codingRuleJpaEntity.category.eq(category) : null;
    }

    /**
     * 심각도 일치 조건
     *
     * @param severity 규칙 심각도
     * @return severity 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression severityEq(RuleSeverity severity) {
        return severity != null ? codingRuleJpaEntity.severity.eq(severity) : null;
    }

    /**
     * 규칙 코드 일치 조건
     *
     * @param code 규칙 코드
     * @return code 일치 조건
     */
    public BooleanExpression codeEq(String code) {
        return codingRuleJpaEntity.code.eq(code);
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건
     */
    public BooleanExpression idNe(Long excludeId) {
        return codingRuleJpaEntity.id.ne(excludeId);
    }

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
     * 카테고리 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 categories가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return categories IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression categoriesIn(CodingRuleSliceCriteria criteria) {
        if (!criteria.hasCategories()) {
            return null;
        }
        return codingRuleJpaEntity.category.in(criteria.categories());
    }

    /**
     * 심각도 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 severities가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return severities IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression severitiesIn(CodingRuleSliceCriteria criteria) {
        if (!criteria.hasSeverities()) {
            return null;
        }
        return codingRuleJpaEntity.severity.in(criteria.severities());
    }

    /**
     * 검색 조건 (필드별)
     *
     * <p>searchField에 따라 해당 필드에서 searchWord를 부분 일치 검색합니다.
     *
     * <p>대소문자 구분 없이 검색합니다.
     *
     * <p>searchField 또는 searchWord가 null이거나 비어있으면 검색하지 않습니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 검색 조건 (nullable이면 null 반환)
     */
    public BooleanExpression searchByField(CodingRuleSliceCriteria criteria) {
        if (!criteria.hasSearch()) {
            return null;
        }

        CodingRuleSearchField field = CodingRuleSearchField.valueOf(criteria.searchField());
        return switch (field) {
            case CODE -> codingRuleJpaEntity.code.containsIgnoreCase(criteria.searchWord());
            case NAME -> codingRuleJpaEntity.name.containsIgnoreCase(criteria.searchWord());
            case DESCRIPTION ->
                    codingRuleJpaEntity.description.containsIgnoreCase(criteria.searchWord());
        };
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(CodingRuleSliceCriteria criteria) {
        return criteria.hasCursor()
                ? codingRuleJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * 키워드 검색 조건 (code, name, description 필드)
     *
     * @param keyword 검색 키워드
     * @return 키워드 포함 조건
     */
    public BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return codingRuleJpaEntity
                .code
                .containsIgnoreCase(keyword)
                .or(codingRuleJpaEntity.name.containsIgnoreCase(keyword))
                .or(codingRuleJpaEntity.description.containsIgnoreCase(keyword));
    }

    /**
     * 심각도 목록 IN 조건 (IndexCriteria 기반)
     *
     * @param criteria 인덱스 조회 조건
     * @return severities IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression severitiesIn(CodingRuleIndexCriteria criteria) {
        if (!criteria.hasSeverities()) {
            return null;
        }
        return codingRuleJpaEntity.severity.in(criteria.severities());
    }

    /**
     * 카테고리 목록 IN 조건 (IndexCriteria 기반)
     *
     * @param criteria 인덱스 조회 조건
     * @return categories IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression categoriesIn(CodingRuleIndexCriteria criteria) {
        if (!criteria.hasCategories()) {
            return null;
        }
        return codingRuleJpaEntity.category.in(criteria.categories());
    }
}
