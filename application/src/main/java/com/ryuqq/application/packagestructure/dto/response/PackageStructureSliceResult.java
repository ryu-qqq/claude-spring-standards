package com.ryuqq.application.packagestructure.dto.response;

import java.util.List;

/**
 * PackageStructureSliceResult - 패키지 구조 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 패키지 구조 목록을 담습니다.
 *
 * @param packageStructures 패키지 구조 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackageStructureSliceResult(
        List<PackageStructureResult> packageStructures, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 PackageStructureSliceResult
     */
    public static PackageStructureSliceResult empty() {
        return new PackageStructureSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param packageStructures 패키지 구조 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return PackageStructureSliceResult
     */
    public static PackageStructureSliceResult of(
            List<PackageStructureResult> packageStructures, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !packageStructures.isEmpty()) {
            nextCursor = packageStructures.get(packageStructures.size() - 1).id();
        }
        return new PackageStructureSliceResult(packageStructures, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 패키지 구조 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return packageStructures.isEmpty();
    }

    /**
     * 패키지 구조 개수 반환
     *
     * @return 패키지 구조 개수
     */
    public int size() {
        return packageStructures.size();
    }
}
