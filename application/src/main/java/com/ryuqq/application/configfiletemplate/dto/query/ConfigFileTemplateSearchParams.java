package com.ryuqq.application.configfiletemplate.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * ConfigFileTemplateSearchParams - ConfigFileTemplate 목록 조회 SearchParams DTO
 *
 * <p>ConfigFileTemplate 목록을 커서 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → Long으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param toolTypes 도구 타입 필터 목록 (CLAUDE, CURSOR 등)
 * @param techStackIds TechStack ID 필터 목록
 * @param architectureIds Architecture ID 필터 목록
 * @param categories 카테고리 필터 목록
 * @param isRequired 필수 파일 여부 필터
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileTemplateSearchParams(
        CommonCursorParams cursorParams,
        List<String> toolTypes,
        List<Long> techStackIds,
        List<Long> architectureIds,
        List<String> categories,
        Boolean isRequired) {

    public ConfigFileTemplateSearchParams {
        toolTypes = toolTypes != null ? List.copyOf(toolTypes) : null;
        techStackIds = techStackIds != null ? List.copyOf(techStackIds) : null;
        architectureIds = architectureIds != null ? List.copyOf(architectureIds) : null;
        categories = categories != null ? List.copyOf(categories) : null;
    }

    /**
     * ConfigFileTemplateSearchParams 생성 (페이징만)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @return ConfigFileTemplateSearchParams 인스턴스
     */
    public static ConfigFileTemplateSearchParams of(CommonCursorParams cursorParams) {
        return new ConfigFileTemplateSearchParams(cursorParams, null, null, null, null, null);
    }

    /**
     * ConfigFileTemplateSearchParams 생성 (필터 포함)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @param toolTypes 도구 타입 필터 목록
     * @param techStackIds TechStack ID 필터 목록
     * @param architectureIds Architecture ID 필터 목록
     * @param categories 카테고리 필터 목록
     * @param isRequired 필수 파일 여부 필터
     * @return ConfigFileTemplateSearchParams 인스턴스
     */
    public static ConfigFileTemplateSearchParams of(
            CommonCursorParams cursorParams,
            List<String> toolTypes,
            List<Long> techStackIds,
            List<Long> architectureIds,
            List<String> categories,
            Boolean isRequired) {
        return new ConfigFileTemplateSearchParams(
                cursorParams, toolTypes, techStackIds, architectureIds, categories, isRequired);
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

    // ==================== Filter Check Methods ====================

    /**
     * 도구 타입 필터가 있는지 확인
     *
     * @return toolTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasToolTypes() {
        return toolTypes != null && !toolTypes.isEmpty();
    }

    /**
     * TechStack ID 필터가 있는지 확인
     *
     * @return techStackIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasTechStackIds() {
        return techStackIds != null && !techStackIds.isEmpty();
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
     * 카테고리 필터가 있는지 확인
     *
     * @return categories가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }

    /**
     * 필수 파일 여부 필터가 있는지 확인
     *
     * @return isRequired가 null이 아니면 true
     */
    public boolean hasIsRequired() {
        return isRequired != null;
    }
}
