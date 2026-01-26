package com.ryuqq.application.ruleexample.dto.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;

/**
 * RuleExampleSearchParams - RuleExample 목록 조회 SearchParams DTO
 *
 * <p>RuleExample 목록을 커서 기반으로 조회하는 SearchParams DTO입니다. 코딩 규칙 ID, 예시 타입, 언어 필터링을 지원합니다.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-004: 목록 조회 SearchParams는 CommonCursorParams 포함 필수.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
 *
 * <p><strong>사용 규칙:</strong>
 *
 * <ul>
 *   <li>CommonCursorParams를 필드로 포함
 *   <li>delegate 메서드를 통해 직접 접근 허용
 *   <li>중첩 접근(params.cursorParams().cursor()) 금지 - delegate 사용
 *   <li>필터 값은 String으로 전달, 도메인 타입 변환은 Factory에서 수행
 * </ul>
 *
 * @param cursorParams 커서 기반 페이징 파라미터
 * @param ruleIds 코딩 규칙 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param exampleTypes 예시 타입 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param languages 언어 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleSearchParams(
        CommonCursorParams cursorParams,
        List<Long> ruleIds,
        List<String> exampleTypes,
        List<String> languages) {

    /**
     * RuleExampleSearchParams 생성 (페이징만)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @return RuleExampleSearchParams 인스턴스
     */
    public static RuleExampleSearchParams of(CommonCursorParams cursorParams) {
        return new RuleExampleSearchParams(cursorParams, null, null, null);
    }

    /**
     * RuleExampleSearchParams 생성 (필터 포함)
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @param ruleIds 코딩 규칙 ID 필터 목록
     * @param exampleTypes 예시 타입 필터 목록
     * @param languages 언어 필터 목록
     * @return RuleExampleSearchParams 인스턴스
     */
    public static RuleExampleSearchParams of(
            CommonCursorParams cursorParams,
            List<Long> ruleIds,
            List<String> exampleTypes,
            List<String> languages) {
        return new RuleExampleSearchParams(cursorParams, ruleIds, exampleTypes, languages);
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

    // ==================== Helper Methods ====================

    /**
     * 코딩 규칙 ID 필터가 있는지 확인
     *
     * @return ruleIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasRuleIds() {
        return ruleIds != null && !ruleIds.isEmpty();
    }

    /**
     * 예시 타입 필터가 있는지 확인
     *
     * @return exampleTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasExampleTypes() {
        return exampleTypes != null && !exampleTypes.isEmpty();
    }

    /**
     * 언어 필터가 있는지 확인
     *
     * @return languages가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasLanguages() {
        return languages != null && !languages.isEmpty();
    }
}
