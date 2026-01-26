package com.ryuqq.application.codingrule.dto.response;

import java.util.List;

/**
 * CodingRuleSliceResult - 코딩 규칙 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 코딩 규칙 목록을 담습니다.
 *
 * @param codingRules 코딩 규칙 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 */
public record CodingRuleSliceResult(
        List<CodingRuleResult> codingRules, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 CodingRuleSliceResult
     */
    public static CodingRuleSliceResult empty() {
        return new CodingRuleSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param codingRules 코딩 규칙 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return CodingRuleSliceResult
     */
    public static CodingRuleSliceResult of(List<CodingRuleResult> codingRules, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !codingRules.isEmpty()) {
            nextCursor = codingRules.get(codingRules.size() - 1).id();
        }
        return new CodingRuleSliceResult(codingRules, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 코딩 규칙 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return codingRules.isEmpty();
    }

    /**
     * 코딩 규칙 개수 반환
     *
     * @return 코딩 규칙 개수
     */
    public int size() {
        return codingRules.size();
    }
}
