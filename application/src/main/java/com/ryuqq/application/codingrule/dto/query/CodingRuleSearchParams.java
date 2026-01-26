package com.ryuqq.application.codingrule.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * CodingRuleSearchParams - CodingRule 목록 조회 SearchParams DTO
 *
 * <p>CodingRule 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 카테고리, 심각도 필터링 및 필드별 검색을 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
 *
 * <p><strong>사용 규칙:</strong>
 *
 * <ul>
 *   <li>CommonCursorParams를 필드로 포함
 *   <li>delegate 메서드를 통해 직접 접근 허용
 *   <li>중첩 접근(params.cursorParams().cursor()) 금지 - delegate 사용
 *   <li>필터 값은 String으로 전달, 도메인 타입 변환은 Factory에서 수행
 *   <li>searchField와 searchWord는 모두 null이 아니고 비어있지 않을 때만 검색 수행
 * </ul>
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param categories 카테고리 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param severities 심각도 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION, null이면 검색 안 함)
 * @param searchWord 검색어 (부분 일치, null이면 검색 안 함)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleSearchParams(
        CommonCursorParams cursorParams,
        List<String> categories,
        List<String> severities,
        String searchField,
        String searchWord) {

    /**
     * CodingRuleSearchParams 생성 (페이징만)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @return CodingRuleSearchParams 인스턴스
     */
    public static CodingRuleSearchParams of(CommonCursorParams cursorParams) {
        return new CodingRuleSearchParams(cursorParams, null, null, null, null);
    }

    /**
     * CodingRuleSearchParams 생성 (필터 포함)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @param categories 카테고리 필터 목록
     * @param severities 심각도 필터 목록
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return CodingRuleSearchParams 인스턴스
     */
    public static CodingRuleSearchParams of(
            CommonCursorParams cursorParams,
            List<String> categories,
            List<String> severities,
            String searchField,
            String searchWord) {
        return new CodingRuleSearchParams(
                cursorParams, categories, severities, searchField, searchWord);
    }

    // ==================== Delegate Methods ====================

    /**
     * 커서 값 반환 (delegate)
     *
     * @return 커서 값 (null 또는 빈 문자열이면 첫 페이지)
     */
    public String cursor() {
        return cursorParams.cursor();
    }

    /**
     * 페이지 크기 반환 (delegate)
     *
     * @return 페이지 크기
     */
    public Integer size() {
        return cursorParams.size();
    }

    /**
     * 첫 페이지인지 확인 (delegate)
     *
     * @return cursor가 null이거나 빈 문자열이면 true
     */
    public boolean isFirstPage() {
        return cursorParams.isFirstPage();
    }

    /**
     * 커서가 있는지 확인 (delegate)
     *
     * @return cursor가 유효한 값이면 true
     */
    public boolean hasCursor() {
        return cursorParams.hasCursor();
    }

    // ==================== Helper Methods ====================

    /**
     * 카테고리 필터가 있는지 확인
     *
     * @return categories가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
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
     * 검색 필드가 있는지 확인
     *
     * @return searchField가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasSearchField() {
        return searchField != null && !searchField.isBlank();
    }

    /**
     * 검색어가 있는지 확인
     *
     * @return searchWord가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasSearchWord() {
        return searchWord != null && !searchWord.isBlank();
    }

    /**
     * 검색 조건이 있는지 확인
     *
     * <p>searchField와 searchWord가 모두 유효할 때만 검색을 수행합니다.
     *
     * @return searchField와 searchWord가 모두 유효하면 true
     */
    public boolean hasSearch() {
        return hasSearchField() && hasSearchWord();
    }
}
