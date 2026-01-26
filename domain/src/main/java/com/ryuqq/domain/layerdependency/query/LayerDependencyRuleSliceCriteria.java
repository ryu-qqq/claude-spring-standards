package com.ryuqq.domain.layerdependency.query;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerDependencyRuleSearchField;
import java.util.List;

/**
 * LayerDependencyRuleSliceCriteria - LayerDependencyRule 슬라이스 조회 조건 (커서 기반)
 *
 * <p>LayerDependencyRule 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param architectureIds 필터링할 아키텍처 ID 목록 (optional)
 * @param dependencyTypes 필터링할 의존성 타입 목록 (optional)
 * @param searchField 검색 필드 (optional)
 * @param searchWord 검색어 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerDependencyRuleSliceCriteria(
        List<ArchitectureId> architectureIds,
        List<DependencyType> dependencyTypes,
        LayerDependencyRuleSearchField searchField,
        String searchWord,
        CursorPageRequest<Long> cursorPageRequest) {

    public LayerDependencyRuleSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 조회)
     *
     * @param size 슬라이스 크기
     * @return LayerDependencyRuleSliceCriteria
     */
    public static LayerDependencyRuleSliceCriteria first(int size) {
        return new LayerDependencyRuleSliceCriteria(
                null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return LayerDependencyRuleSliceCriteria
     */
    public static LayerDependencyRuleSliceCriteria afterId(Long cursorId, int size) {
        return new LayerDependencyRuleSliceCriteria(
                null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 커서 기반 페이징 요청 생성
     *
     * @param architectureIds 아키텍처 ID 목록 (nullable)
     * @param dependencyTypes 의존성 타입 목록 (nullable)
     * @param searchField 검색 필드 (nullable)
     * @param searchWord 검색어 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return LayerDependencyRuleSliceCriteria
     */
    public static LayerDependencyRuleSliceCriteria of(
            List<ArchitectureId> architectureIds,
            List<DependencyType> dependencyTypes,
            LayerDependencyRuleSearchField searchField,
            String searchWord,
            CursorPageRequest<Long> cursorPageRequest) {
        return new LayerDependencyRuleSliceCriteria(
                architectureIds, dependencyTypes, searchField, searchWord, cursorPageRequest);
    }

    /**
     * 아키텍처 ID 필터 존재 여부 확인
     *
     * @return architectureIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasArchitectureFilter() {
        return architectureIds != null && !architectureIds.isEmpty();
    }

    /**
     * 의존성 타입 필터 존재 여부 확인
     *
     * @return dependencyTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasDependencyTypeFilter() {
        return dependencyTypes != null && !dependencyTypes.isEmpty();
    }

    /**
     * 검색 조건 존재 여부 확인
     *
     * @return searchField와 searchWord가 모두 있으면 true
     */
    public boolean hasSearch() {
        return searchField != null && searchWord != null && !searchWord.isBlank();
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
