package com.ryuqq.application.packagepurpose.dto.response;

import java.util.List;

/**
 * PackagePurposeSliceResult - 패키지 목적 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 패키지 목적 목록을 담습니다.
 *
 * @param packagePurposes 패키지 목적 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 */
public record PackagePurposeSliceResult(
        List<PackagePurposeResult> packagePurposes, boolean hasNext, Long nextCursor) {

    public static PackagePurposeSliceResult empty() {
        return new PackagePurposeSliceResult(List.of(), false, null);
    }

    public static PackagePurposeSliceResult of(
            List<PackagePurposeResult> packagePurposes, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !packagePurposes.isEmpty()) {
            nextCursor = packagePurposes.get(packagePurposes.size() - 1).id();
        }
        return new PackagePurposeSliceResult(packagePurposes, hasNext, nextCursor);
    }

    public boolean isEmpty() {
        return packagePurposes.isEmpty();
    }

    public int size() {
        return packagePurposes.size();
    }
}
