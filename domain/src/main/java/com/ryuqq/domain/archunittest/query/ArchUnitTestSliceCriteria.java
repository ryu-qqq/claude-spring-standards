package com.ryuqq.domain.archunittest.query;

import com.ryuqq.domain.archunittest.vo.ArchUnitTestSearchField;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;

/**
 * ArchUnitTestSliceCriteria - ArchUnitTest 슬라이스 조회 조건 (커서 기반)
 *
 * <p>ArchUnitTest 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ArchUnitTest ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param structureIds 필터링할 패키지 구조 ID 목록 (optional)
 * @param searchField 검색 필드 (optional)
 * @param searchWord 검색어 (optional)
 * @param severities 심각도 필터 목록 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 */
public record ArchUnitTestSliceCriteria(
        List<PackageStructureId> structureIds,
        ArchUnitTestSearchField searchField,
        String searchWord,
        List<ArchUnitTestSeverity> severities,
        CursorPageRequest<Long> cursorPageRequest) {

    public ArchUnitTestSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 패키지 구조)
     *
     * @param size 슬라이스 크기
     * @return ArchUnitTestSliceCriteria
     */
    public static ArchUnitTestSliceCriteria first(int size) {
        return new ArchUnitTestSliceCriteria(null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ArchUnitTestSliceCriteria
     */
    public static ArchUnitTestSliceCriteria afterId(Long cursorId, int size) {
        return new ArchUnitTestSliceCriteria(
                null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 커서 기반 페이징 요청 생성
     *
     * @param structureIds 패키지 구조 ID 목록 (nullable)
     * @param searchField 검색 필드 (nullable)
     * @param searchWord 검색어 (nullable)
     * @param severities 심각도 필터 목록 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ArchUnitTestSliceCriteria
     */
    public static ArchUnitTestSliceCriteria of(
            List<PackageStructureId> structureIds,
            ArchUnitTestSearchField searchField,
            String searchWord,
            List<ArchUnitTestSeverity> severities,
            CursorPageRequest<Long> cursorPageRequest) {
        return new ArchUnitTestSliceCriteria(
                structureIds, searchField, searchWord, severities, cursorPageRequest);
    }

    /**
     * 패키지 구조 ID 필터 존재 여부 확인
     *
     * @return structureIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasStructureFilter() {
        return structureIds != null && !structureIds.isEmpty();
    }

    public boolean hasSearch() {
        return searchField != null && searchWord != null && !searchWord.isBlank();
    }

    public boolean hasSeverities() {
        return severities != null && !severities.isEmpty();
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
