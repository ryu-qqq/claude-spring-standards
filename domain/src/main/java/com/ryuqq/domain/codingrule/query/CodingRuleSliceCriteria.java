package com.ryuqq.domain.codingrule.query;

import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;

/**
 * CodingRuleSliceCriteria - CodingRule 슬라이스 조회 조건 (커서 기반)
 *
 * <p>CodingRule 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p>카테고리, 심각도 필터링 및 필드별 검색을 지원합니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 CodingRule ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param categories 카테고리 필터 목록 (필터링 용도, nullable)
 * @param severities 심각도 필터 목록 (필터링 용도, nullable)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION, nullable)
 * @param searchWord 검색어 (부분 일치, nullable)
 * @param cursorPageRequest 커서 기반 페이징 요청 (ID 기반: Long)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleSliceCriteria(
        List<RuleCategory> categories,
        List<RuleSeverity> severities,
        String searchField,
        String searchWord,
        CursorPageRequest<Long> cursorPageRequest) {

    public CodingRuleSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return CodingRuleSliceCriteria
     */
    public static CodingRuleSliceCriteria first(int size) {
        return new CodingRuleSliceCriteria(null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return CodingRuleSliceCriteria
     */
    public static CodingRuleSliceCriteria afterId(Long cursorId, int size) {
        return new CodingRuleSliceCriteria(
                null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * CodingRuleSliceCriteria 생성 (static factory method)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return CodingRuleSliceCriteria 인스턴스
     */
    public static CodingRuleSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new CodingRuleSliceCriteria(null, null, null, null, cursorPageRequest);
    }

    /**
     * CodingRuleSliceCriteria 생성 (필터 포함)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param categories 카테고리 필터 목록
     * @param severities 심각도 필터 목록
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return CodingRuleSliceCriteria 인스턴스
     */
    public static CodingRuleSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest,
            List<RuleCategory> categories,
            List<RuleSeverity> severities,
            String searchField,
            String searchWord) {
        return new CodingRuleSliceCriteria(
                categories, severities, searchField, searchWord, cursorPageRequest);
    }

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

    /**
     * 첫 페이지 요청인지 확인
     *
     * @return cursor가 null이면 true
     */
    public boolean isFirstPage() {
        return cursorPageRequest.cursor() == null;
    }

    /**
     * 커서가 있는지 확인
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursorPageRequest.cursor() != null;
    }

    /**
     * 슬라이스 크기 반환 (편의 메서드)
     *
     * @return size
     */
    public int size() {
        return cursorPageRequest.size();
    }

    /**
     * 실제 조회 크기 반환 (hasNext 판단용 +1)
     *
     * @return size + 1
     */
    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }
}
