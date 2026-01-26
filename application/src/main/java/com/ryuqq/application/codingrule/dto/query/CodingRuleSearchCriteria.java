package com.ryuqq.application.codingrule.dto.query;

import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;

/**
 * CodingRuleSearchCriteria - 코딩 규칙 검색 조건
 *
 * <p>코딩 규칙 검색을 위한 조건을 정의합니다.
 *
 * <p>Zero-Tolerance 여부는 ZeroToleranceRule 엔티티의 존재 여부로 판단하므로 이 검색 조건에서 제외됩니다. Zero-Tolerance 규칙을
 * 검색하려면 ZeroToleranceRuleQueryPort를 사용하세요.
 *
 * @param conventionId 컨벤션 ID (nullable)
 * @param category 카테고리 (nullable)
 * @param severity 심각도 (nullable)
 * @author ryu-qqq
 */
public record CodingRuleSearchCriteria(
        Long conventionId, RuleCategory category, RuleSeverity severity) {

    /**
     * 빈 검색 조건 생성
     *
     * @return 모든 조건이 null인 검색 조건
     */
    public static CodingRuleSearchCriteria empty() {
        return new CodingRuleSearchCriteria(null, null, null);
    }

    /**
     * Convention ID로만 검색하는 조건 생성
     *
     * @param conventionId 컨벤션 ID
     * @return 검색 조건
     */
    public static CodingRuleSearchCriteria byConventionId(Long conventionId) {
        return new CodingRuleSearchCriteria(conventionId, null, null);
    }

    /**
     * 조건이 모두 비어있는지 확인
     *
     * @return 모든 조건이 null이면 true
     */
    public boolean isEmpty() {
        return conventionId == null && category == null && severity == null;
    }
}
