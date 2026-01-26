package com.ryuqq.application.layerdependency.dto.response;

import java.util.List;

/**
 * LayerDependencyRuleSliceResult - 레이어 의존성 규칙 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 레이어 의존성 규칙 목록을 담습니다.
 *
 * @param rules 레이어 의존성 규칙 목록
 * @param size 페이지 크기
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerDependencyRuleSliceResult(
        List<LayerDependencyRuleResult> rules, int size, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 LayerDependencyRuleSliceResult
     */
    public static LayerDependencyRuleSliceResult empty(int size) {
        return new LayerDependencyRuleSliceResult(List.of(), size, false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param rules 레이어 의존성 규칙 목록
     * @param size 페이지 크기
     * @param hasNext 다음 페이지 존재 여부
     * @return LayerDependencyRuleSliceResult
     */
    public static LayerDependencyRuleSliceResult of(
            List<LayerDependencyRuleResult> rules, int size, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !rules.isEmpty()) {
            nextCursor = rules.get(rules.size() - 1).id();
        }
        return new LayerDependencyRuleSliceResult(rules, size, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 레이어 의존성 규칙 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return rules.isEmpty();
    }

    /**
     * 레이어 의존성 규칙 개수 반환
     *
     * @return 레이어 의존성 규칙 개수
     */
    public int count() {
        return rules.size();
    }
}
