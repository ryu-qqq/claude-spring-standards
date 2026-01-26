package com.ryuqq.domain.classtype.query;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;

/**
 * ClassTypeSliceCriteria - ClassType 슬라이스 조회 조건 (커서 기반)
 *
 * <p>ClassType 목록 조회 시 사용되는 커서 기반 페이징 조건입니다.
 *
 * <p>Category ID, Architecture ID 필터링 및 필드별 검색을 지원합니다.
 *
 * @param ids ID 목록 (특정 ID 조회, nullable)
 * @param categoryIds Category ID 목록 (필터링 용도, nullable)
 * @param architectureIds Architecture ID 목록 (필터링 용도, nullable)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION, nullable)
 * @param searchWord 검색어 (부분 일치, nullable)
 * @param cursorPageRequest 커서 기반 페이징 요청 (ID 기반: Long)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ClassTypeSliceCriteria(
        List<ClassTypeId> ids,
        List<ClassTypeCategoryId> categoryIds,
        List<ArchitectureId> architectureIds,
        String searchField,
        String searchWord,
        CursorPageRequest<Long> cursorPageRequest) {

    public ClassTypeSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return ClassTypeSliceCriteria
     */
    public static ClassTypeSliceCriteria first(int size) {
        return new ClassTypeSliceCriteria(
                null, null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * Category ID 필터로 첫 페이지 슬라이스 조건 생성
     *
     * @param categoryId Category ID
     * @param size 슬라이스 크기
     * @return ClassTypeSliceCriteria
     */
    public static ClassTypeSliceCriteria firstByCategory(ClassTypeCategoryId categoryId, int size) {
        return new ClassTypeSliceCriteria(
                null, List.of(categoryId), null, null, null, CursorPageRequest.first(size));
    }

    /**
     * Architecture ID 필터로 첫 페이지 슬라이스 조건 생성
     *
     * @param architectureId Architecture ID
     * @param size 슬라이스 크기
     * @return ClassTypeSliceCriteria
     */
    public static ClassTypeSliceCriteria firstByArchitecture(
            ArchitectureId architectureId, int size) {
        return new ClassTypeSliceCriteria(
                null, null, List.of(architectureId), null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ClassTypeSliceCriteria
     */
    public static ClassTypeSliceCriteria afterId(Long cursorId, int size) {
        return new ClassTypeSliceCriteria(
                null, null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * ClassTypeSliceCriteria 생성 (static factory method)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param ids ID 목록
     * @param categoryIds Category ID 목록
     * @param architectureIds Architecture ID 목록
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return ClassTypeSliceCriteria 인스턴스
     */
    public static ClassTypeSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest,
            List<ClassTypeId> ids,
            List<ClassTypeCategoryId> categoryIds,
            List<ArchitectureId> architectureIds,
            String searchField,
            String searchWord) {
        return new ClassTypeSliceCriteria(
                ids, categoryIds, architectureIds, searchField, searchWord, cursorPageRequest);
    }

    /**
     * ID 필터가 있는지 확인
     *
     * @return ids가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasIds() {
        return ids != null && !ids.isEmpty();
    }

    /**
     * Category ID 필터가 있는지 확인
     *
     * @return categoryIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCategoryIds() {
        return categoryIds != null && !categoryIds.isEmpty();
    }

    /**
     * Architecture ID 필터가 있는지 확인
     *
     * @return architectureIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasArchitectureIds() {
        return architectureIds != null && !architectureIds.isEmpty();
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
