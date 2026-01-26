package com.ryuqq.application.layerdependency.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * LayerDependencyRuleSearchParams - LayerDependencyRule 목록 조회 SearchParams DTO
 *
 * <p>LayerDependencyRule 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 아키텍처 ID(복수), 의존성 타입(복수), 검색(필드/키워드)
 * 필터링을 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param architectureIds 아키텍처 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param dependencyTypes 의존성 타입 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (nullable)
 * @param searchWord 검색어 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerDependencyRuleSearchParams(
        CommonCursorParams cursorParams,
        List<Long> architectureIds,
        List<String> dependencyTypes,
        String searchField,
        String searchWord) {

    public static LayerDependencyRuleSearchParams of(CommonCursorParams cursorParams) {
        return new LayerDependencyRuleSearchParams(cursorParams, null, null, null, null);
    }

    public static LayerDependencyRuleSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> architectureIds,
            List<String> dependencyTypes,
            String searchField,
            String searchWord) {
        return new LayerDependencyRuleSearchParams(
                cursorParams, architectureIds, dependencyTypes, searchField, searchWord);
    }

    // ==================== Delegate Methods ====================
    public String cursor() {
        return cursorParams.cursor();
    }

    public Integer size() {
        return cursorParams.size();
    }

    public boolean isFirstPage() {
        return cursorParams.isFirstPage();
    }

    public boolean hasCursor() {
        return cursorParams.hasCursor();
    }

    // ==================== Helper Methods ====================
    public boolean hasArchitectureIds() {
        return architectureIds != null && !architectureIds.isEmpty();
    }

    public boolean hasDependencyTypes() {
        return dependencyTypes != null && !dependencyTypes.isEmpty();
    }

    public boolean hasSearch() {
        return searchField != null
                && !searchField.isBlank()
                && searchWord != null
                && !searchWord.isBlank();
    }
}
