package com.ryuqq.domain.codingrule.query;

import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.util.List;

/**
 * CodingRuleIndexCriteria - 코딩 규칙 인덱스 조회 조건
 *
 * <p>규칙 인덱스(code, name, severity, category)만 조회할 때 사용하는 조건입니다.
 *
 * <p>전체 규칙 상세가 아닌 인덱스만 조회하여 캐싱 효율성을 높입니다.
 *
 * @param conventionId 컨벤션 ID (null이면 전체)
 * @param severities 심각도 필터 목록 (null이면 전체)
 * @param categories 카테고리 필터 목록 (null이면 전체)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleIndexCriteria(
        Long conventionId, List<RuleSeverity> severities, List<RuleCategory> categories) {

    /**
     * 전체 인덱스 조회 (필터 없음)
     *
     * @return CodingRuleIndexCriteria
     */
    public static CodingRuleIndexCriteria all() {
        return new CodingRuleIndexCriteria(null, null, null);
    }

    /**
     * 컨벤션 ID로 필터링
     *
     * @param conventionId 컨벤션 ID
     * @return CodingRuleIndexCriteria
     */
    public static CodingRuleIndexCriteria byConventionId(Long conventionId) {
        return new CodingRuleIndexCriteria(conventionId, null, null);
    }

    /**
     * 전체 필터 조건으로 생성
     *
     * @param conventionId 컨벤션 ID (null 가능)
     * @param severities 심각도 필터 목록 (null 가능)
     * @param categories 카테고리 필터 목록 (null 가능)
     * @return CodingRuleIndexCriteria
     */
    public static CodingRuleIndexCriteria of(
            Long conventionId, List<RuleSeverity> severities, List<RuleCategory> categories) {
        return new CodingRuleIndexCriteria(conventionId, severities, categories);
    }

    /**
     * 컨벤션 ID 필터가 있는지 확인
     *
     * @return conventionId가 null이 아니면 true
     */
    public boolean hasConventionId() {
        return conventionId != null;
    }

    /**
     * 심각도 필터가 있는지 확인
     *
     * @return severities가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasSeverities() {
        return severities != null && !severities.isEmpty();
    }

    /**
     * 카테고리 필터가 있는지 확인
     *
     * @return categories가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }
}
