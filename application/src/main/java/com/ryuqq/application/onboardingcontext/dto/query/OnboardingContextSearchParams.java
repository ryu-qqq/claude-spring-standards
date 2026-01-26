package com.ryuqq.application.onboardingcontext.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * OnboardingContextSearchParams - OnboardingContext 목록 조회 SearchParams DTO
 *
 * <p>OnboardingContext 목록을 커서 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → Long으로 전달, Factory에서 변환.
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param techStackIds TechStack ID 필터 목록
 * @param architectureIds Architecture ID 필터 목록
 * @param contextTypes 컨텍스트 타입 필터 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record OnboardingContextSearchParams(
        CommonCursorParams cursorParams,
        List<Long> techStackIds,
        List<Long> architectureIds,
        List<String> contextTypes) {

    public OnboardingContextSearchParams {
        techStackIds = techStackIds != null ? List.copyOf(techStackIds) : null;
        architectureIds = architectureIds != null ? List.copyOf(architectureIds) : null;
        contextTypes = contextTypes != null ? List.copyOf(contextTypes) : null;
    }

    /**
     * OnboardingContextSearchParams 생성 (페이징만)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @return OnboardingContextSearchParams 인스턴스
     */
    public static OnboardingContextSearchParams of(CommonCursorParams cursorParams) {
        return new OnboardingContextSearchParams(cursorParams, null, null, null);
    }

    /**
     * OnboardingContextSearchParams 생성 (필터 포함)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @param techStackIds TechStack ID 필터 목록
     * @param architectureIds Architecture ID 필터 목록
     * @param contextTypes 컨텍스트 타입 필터 목록
     * @return OnboardingContextSearchParams 인스턴스
     */
    public static OnboardingContextSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> techStackIds,
            List<Long> architectureIds,
            List<String> contextTypes) {
        return new OnboardingContextSearchParams(
                cursorParams, techStackIds, architectureIds, contextTypes);
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
     * 컨텍스트 타입 필터가 있는지 확인
     *
     * @return contextTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasContextTypes() {
        return contextTypes != null && !contextTypes.isEmpty();
    }
}
