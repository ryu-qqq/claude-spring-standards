package com.ryuqq.application.packagepurpose.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * PackagePurposeSearchParams - PackagePurpose 목록 조회 SearchParams DTO
 *
 * <p>PackagePurpose 목록을 커서 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>구조 ID(복수) 필터링과 검색(필드/키워드)을 지원합니다.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param structureIds 패키지 구조 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION)
 * @param searchWord 검색어
 * @author ryu-qqq
 */
public record PackagePurposeSearchParams(
        CommonCursorParams cursorParams,
        List<Long> structureIds,
        String searchField,
        String searchWord) {

    public static PackagePurposeSearchParams of(CommonCursorParams cursorParams) {
        return new PackagePurposeSearchParams(cursorParams, null, null, null);
    }

    public static PackagePurposeSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> structureIds,
            String searchField,
            String searchWord) {
        return new PackagePurposeSearchParams(cursorParams, structureIds, searchField, searchWord);
    }

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

    public boolean hasStructureIds() {
        return structureIds != null && !structureIds.isEmpty();
    }

    public boolean hasSearch() {
        return searchField != null
                && !searchField.isBlank()
                && searchWord != null
                && !searchWord.isBlank();
    }
}
