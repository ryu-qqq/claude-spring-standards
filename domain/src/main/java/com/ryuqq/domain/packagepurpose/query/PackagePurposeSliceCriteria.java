package com.ryuqq.domain.packagepurpose.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.packagepurpose.vo.PackagePurposeSearchField;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;

/**
 * PackagePurposeSliceCriteria - PackagePurpose 슬라이스 조회 조건 (커서 기반)
 *
 * <p>PackagePurpose 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p>패키지 구조 ID(복수) 필터링과 검색(필드/키워드)을 지원합니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 PackagePurpose ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param structureIds 패키지 구조 ID 필터 목록 (nullable)
 * @param searchField 검색 필드 (nullable)
 * @param searchWord 검색어 (nullable)
 * @param cursorPageRequest 커서 기반 페이징 요청 (ID 기반: Long)
 * @author ryu-qqq
 */
public record PackagePurposeSliceCriteria(
        List<PackageStructureId> structureIds,
        PackagePurposeSearchField searchField,
        String searchWord,
        CursorPageRequest<Long> cursorPageRequest) {

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return PackagePurposeSliceCriteria
     */
    public static PackagePurposeSliceCriteria first(int size) {
        return new PackagePurposeSliceCriteria(null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return PackagePurposeSliceCriteria
     */
    public static PackagePurposeSliceCriteria afterId(Long cursorId, int size) {
        return new PackagePurposeSliceCriteria(
                null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * PackagePurposeSliceCriteria 생성 (static factory method)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return PackagePurposeSliceCriteria 인스턴스
     */
    public static PackagePurposeSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new PackagePurposeSliceCriteria(null, null, null, cursorPageRequest);
    }

    /**
     * PackagePurposeSliceCriteria 생성 (필터 포함)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param structureIds 패키지 구조 ID 필터 목록
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return PackagePurposeSliceCriteria 인스턴스
     */
    public static PackagePurposeSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest,
            List<PackageStructureId> structureIds,
            PackagePurposeSearchField searchField,
            String searchWord) {
        return new PackagePurposeSliceCriteria(
                structureIds, searchField, searchWord, cursorPageRequest);
    }

    public boolean hasStructureIds() {
        return structureIds != null && !structureIds.isEmpty();
    }

    public boolean hasSearch() {
        return searchField != null && searchWord != null && !searchWord.isBlank();
    }

    public boolean hasCursor() {
        return cursorPageRequest.cursor() != null;
    }

    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }
}
