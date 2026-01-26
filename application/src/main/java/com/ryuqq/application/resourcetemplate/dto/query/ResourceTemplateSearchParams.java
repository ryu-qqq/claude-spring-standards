package com.ryuqq.application.resourcetemplate.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * ResourceTemplateSearchParams - ResourceTemplate 목록 조회 SearchParams DTO
 *
 * <p>ResourceTemplate 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 모듈 ID(복수), 카테고리(복수), 파일 타입(복수) 필터링을
 * 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String/Long으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param moduleIds 모듈 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param categories 카테고리 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param fileTypes 파일 타입 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ResourceTemplateSearchParams(
        CommonCursorParams cursorParams,
        List<Long> moduleIds,
        List<String> categories,
        List<String> fileTypes) {

    public static ResourceTemplateSearchParams of(CommonCursorParams cursorParams) {
        return new ResourceTemplateSearchParams(cursorParams, null, null, null);
    }

    public static ResourceTemplateSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> moduleIds,
            List<String> categories,
            List<String> fileTypes) {
        return new ResourceTemplateSearchParams(cursorParams, moduleIds, categories, fileTypes);
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
    public boolean hasModuleIds() {
        return moduleIds != null && !moduleIds.isEmpty();
    }

    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }

    public boolean hasFileTypes() {
        return fileTypes != null && !fileTypes.isEmpty();
    }
}
