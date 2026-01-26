package com.ryuqq.application.archunittest.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * ArchUnitTestSearchParams - ArchUnitTest 목록 조회 SearchParams DTO
 *
 * <p>ArchUnitTest 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 패키지 구조 ID(복수), 검색 필드/검색어, 심각도(복수) 필터링을
 * 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param structureIds 패키지 구조 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (nullable)
 * @param searchWord 검색어 (nullable)
 * @param severities 심각도 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchUnitTestSearchParams(
        CommonCursorParams cursorParams,
        List<Long> structureIds,
        String searchField,
        String searchWord,
        List<String> severities) {

    public static ArchUnitTestSearchParams of(CommonCursorParams cursorParams) {
        return new ArchUnitTestSearchParams(cursorParams, null, null, null, null);
    }

    public static ArchUnitTestSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> structureIds,
            String searchField,
            String searchWord,
            List<String> severities) {
        return new ArchUnitTestSearchParams(
                cursorParams, structureIds, searchField, searchWord, severities);
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
    public boolean hasStructureIds() {
        return structureIds != null && !structureIds.isEmpty();
    }

    public boolean hasSearch() {
        return searchField != null
                && !searchField.isBlank()
                && searchWord != null
                && !searchWord.isBlank();
    }

    public boolean hasSeverities() {
        return severities != null && !severities.isEmpty();
    }
}
