package com.ryuqq.application.ruleexample.dto.response;

import java.util.List;

/**
 * RuleExampleSliceResult - 규칙 예시 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 규칙 예시 목록을 담습니다.
 *
 * @param ruleExamples 규칙 예시 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleSliceResult(
        List<RuleExampleResult> ruleExamples, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 RuleExampleSliceResult
     */
    public static RuleExampleSliceResult empty() {
        return new RuleExampleSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param ruleExamples 규칙 예시 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return RuleExampleSliceResult
     */
    public static RuleExampleSliceResult of(List<RuleExampleResult> ruleExamples, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !ruleExamples.isEmpty()) {
            nextCursor = ruleExamples.get(ruleExamples.size() - 1).id();
        }
        return new RuleExampleSliceResult(ruleExamples, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 규칙 예시 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return ruleExamples.isEmpty();
    }

    /**
     * 규칙 예시 개수 반환
     *
     * @return 규칙 예시 개수
     */
    public int size() {
        return ruleExamples.size();
    }
}
