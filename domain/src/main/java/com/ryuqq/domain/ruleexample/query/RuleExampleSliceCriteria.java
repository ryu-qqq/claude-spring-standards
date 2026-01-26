package com.ryuqq.domain.ruleexample.query;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import java.util.List;

/**
 * RuleExampleSliceCriteria - RuleExample 슬라이스 조회 조건 (커서 기반)
 *
 * <p>RuleExample 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 RuleExample ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param ruleIds 필터링할 코딩 규칙 ID 목록 (optional)
 * @param exampleTypes 필터링할 예시 타입 목록 (optional)
 * @param languages 필터링할 언어 목록 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleSliceCriteria(
        List<CodingRuleId> ruleIds,
        List<ExampleType> exampleTypes,
        List<ExampleLanguage> languages,
        CursorPageRequest<Long> cursorPageRequest) {

    public RuleExampleSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 규칙)
     *
     * @param size 슬라이스 크기
     * @return RuleExampleSliceCriteria
     */
    public static RuleExampleSliceCriteria first(int size) {
        return new RuleExampleSliceCriteria(null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return RuleExampleSliceCriteria
     */
    public static RuleExampleSliceCriteria afterId(Long cursorId, int size) {
        return new RuleExampleSliceCriteria(
                null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 커서 기반 페이징 요청 생성
     *
     * @param ruleIds 코딩 규칙 ID 목록 (nullable)
     * @param exampleTypes 예시 타입 목록 (nullable)
     * @param languages 언어 목록 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return RuleExampleSliceCriteria
     */
    public static RuleExampleSliceCriteria of(
            List<CodingRuleId> ruleIds,
            List<ExampleType> exampleTypes,
            List<ExampleLanguage> languages,
            CursorPageRequest<Long> cursorPageRequest) {
        return new RuleExampleSliceCriteria(ruleIds, exampleTypes, languages, cursorPageRequest);
    }

    /**
     * 코딩 규칙 ID 필터 존재 여부 확인
     *
     * @return ruleIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasRuleIds() {
        return ruleIds != null && !ruleIds.isEmpty();
    }

    /**
     * 예시 타입 필터 존재 여부 확인
     *
     * @return exampleTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasExampleTypes() {
        return exampleTypes != null && !exampleTypes.isEmpty();
    }

    /**
     * 언어 필터 존재 여부 확인
     *
     * @return languages가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasLanguages() {
        return languages != null && !languages.isEmpty();
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
