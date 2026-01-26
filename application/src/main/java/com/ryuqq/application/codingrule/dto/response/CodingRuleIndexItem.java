package com.ryuqq.application.codingrule.dto.response;

/**
 * CodingRuleIndexItem - 코딩 규칙 인덱스 항목 DTO
 *
 * <p>캐싱 효율성을 위한 경량 DTO입니다. 핵심 정보만 포함합니다.
 *
 * <p>상세 정보는 get_rule(code) API로 개별 조회합니다.
 *
 * @param code 규칙 코드 (예: DOM-AGG-001)
 * @param name 규칙 이름
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR)
 * @param category 카테고리 (STRUCTURE, NAMING, DEPENDENCY 등)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleIndexItem(String code, String name, String severity, String category) {

    /**
     * CodingRuleIndexItem 생성
     *
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @return CodingRuleIndexItem
     */
    public static CodingRuleIndexItem of(
            String code, String name, String severity, String category) {
        return new CodingRuleIndexItem(code, name, severity, category);
    }
}
