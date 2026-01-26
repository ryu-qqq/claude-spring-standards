package com.ryuqq.application.module.dto.response;

import java.util.List;

/**
 * ModuleSliceResult - 모듈 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 모듈 목록을 담습니다.
 *
 * @param modules 모듈 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 */
public record ModuleSliceResult(List<ModuleResult> modules, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 ModuleSliceResult
     */
    public static ModuleSliceResult empty() {
        return new ModuleSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param modules 모듈 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return ModuleSliceResult
     */
    public static ModuleSliceResult of(List<ModuleResult> modules, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !modules.isEmpty()) {
            nextCursor = modules.get(modules.size() - 1).moduleId();
        }
        return new ModuleSliceResult(modules, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 모듈 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return modules.isEmpty();
    }

    /**
     * 모듈 개수 반환
     *
     * @return 모듈 개수
     */
    public int size() {
        return modules.size();
    }
}
