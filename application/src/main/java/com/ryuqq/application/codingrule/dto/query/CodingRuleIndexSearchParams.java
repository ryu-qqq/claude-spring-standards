package com.ryuqq.application.codingrule.dto.query;

import java.util.List;

/**
 * CodingRuleIndexSearchParams - 코딩 규칙 인덱스 조회 파라미터
 *
 * <p>규칙 인덱스(code, name, severity, category)만 조회할 때 사용합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달.
 *
 * @param conventionId 컨벤션 ID (null이면 전체)
 * @param severities 심각도 필터 목록 (String: BLOCKER, CRITICAL, MAJOR, MINOR)
 * @param categories 카테고리 필터 목록 (String: STRUCTURE, NAMING, DEPENDENCY, etc.)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleIndexSearchParams(
        Long conventionId, List<String> severities, List<String> categories) {

    /**
     * 전체 인덱스 조회 (필터 없음)
     *
     * @return CodingRuleIndexSearchParams
     */
    public static CodingRuleIndexSearchParams all() {
        return new CodingRuleIndexSearchParams(null, null, null);
    }

    /**
     * 컨벤션 ID로 필터링
     *
     * @param conventionId 컨벤션 ID
     * @return CodingRuleIndexSearchParams
     */
    public static CodingRuleIndexSearchParams byConventionId(Long conventionId) {
        return new CodingRuleIndexSearchParams(conventionId, null, null);
    }

    /**
     * 전체 필터 조건으로 생성
     *
     * @param conventionId 컨벤션 ID (null 가능)
     * @param severities 심각도 필터 목록 (null 가능)
     * @param categories 카테고리 필터 목록 (null 가능)
     * @return CodingRuleIndexSearchParams
     */
    public static CodingRuleIndexSearchParams of(
            Long conventionId, List<String> severities, List<String> categories) {
        return new CodingRuleIndexSearchParams(conventionId, severities, categories);
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
