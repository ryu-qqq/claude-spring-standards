package com.ryuqq.application.zerotolerance.dto.response;

import java.util.List;

/**
 * ZeroToleranceRuleSliceResult - Zero-Tolerance 규칙 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 Zero-Tolerance 규칙 상세 목록을 담습니다.
 *
 * @param rules Zero-Tolerance 규칙 상세 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursorId 다음 페이지 커서 ID (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceRuleSliceResult(
        List<ZeroToleranceRuleDetailResult> rules, boolean hasNext, Long nextCursorId) {

    public ZeroToleranceRuleSliceResult {
        if (rules == null) {
            rules = List.of();
        }
    }

    /**
     * 빈 결과 생성
     *
     * @return 빈 ZeroToleranceRuleSliceResult
     */
    public static ZeroToleranceRuleSliceResult empty() {
        return new ZeroToleranceRuleSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param rules Zero-Tolerance 규칙 상세 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return ZeroToleranceRuleSliceResult
     */
    public static ZeroToleranceRuleSliceResult of(
            List<ZeroToleranceRuleDetailResult> rules, boolean hasNext) {
        Long nextCursorId = null;
        if (hasNext && !rules.isEmpty()) {
            nextCursorId = rules.get(rules.size() - 1).codingRule().id();
        }
        return new ZeroToleranceRuleSliceResult(rules, hasNext, nextCursorId);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 규칙 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return rules.isEmpty();
    }

    /**
     * 규칙 개수 반환
     *
     * @return 규칙 개수
     */
    public int size() {
        return rules.size();
    }
}
