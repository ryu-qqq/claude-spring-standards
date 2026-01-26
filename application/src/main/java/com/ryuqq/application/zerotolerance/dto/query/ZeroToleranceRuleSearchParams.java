package com.ryuqq.application.zerotolerance.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * ZeroToleranceRuleSearchParams - ZeroToleranceRule 목록 조회 SearchParams DTO
 *
 * <p>ZeroToleranceRule 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 컨벤션 ID(복수), 탐지 방식(복수), 검색(필드/키워드), PR
 * 자동 거부 여부 필터링을 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param conventionIds 컨벤션 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param detectionTypes 탐지 방식 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (nullable)
 * @param searchWord 검색어 (nullable)
 * @param autoRejectPr PR 자동 거부 여부 필터 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceRuleSearchParams(
        CommonCursorParams cursorParams,
        List<Long> conventionIds,
        List<String> detectionTypes,
        String searchField,
        String searchWord,
        Boolean autoRejectPr) {

    public static ZeroToleranceRuleSearchParams of(CommonCursorParams cursorParams) {
        return new ZeroToleranceRuleSearchParams(cursorParams, null, null, null, null, null);
    }

    public static ZeroToleranceRuleSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> conventionIds,
            List<String> detectionTypes,
            String searchField,
            String searchWord,
            Boolean autoRejectPr) {
        return new ZeroToleranceRuleSearchParams(
                cursorParams, conventionIds, detectionTypes, searchField, searchWord, autoRejectPr);
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
    public boolean hasConventionIds() {
        return conventionIds != null && !conventionIds.isEmpty();
    }

    public boolean hasDetectionTypes() {
        return detectionTypes != null && !detectionTypes.isEmpty();
    }

    public boolean hasSearch() {
        return searchField != null
                && !searchField.isBlank()
                && searchWord != null
                && !searchWord.isBlank();
    }

    public boolean hasAutoRejectPr() {
        return autoRejectPr != null;
    }
}
