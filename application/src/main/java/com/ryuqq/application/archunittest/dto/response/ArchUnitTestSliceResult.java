package com.ryuqq.application.archunittest.dto.response;

import java.util.List;

/**
 * ArchUnitTestSliceResult - ArchUnit 테스트 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 ArchUnit 테스트 목록을 담습니다.
 *
 * @param archUnitTests ArchUnit 테스트 목록
 * @param size 페이지 크기
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchUnitTestSliceResult(
        List<ArchUnitTestResult> archUnitTests, int size, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 ArchUnitTestSliceResult
     */
    public static ArchUnitTestSliceResult empty(int size) {
        return new ArchUnitTestSliceResult(List.of(), size, false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param archUnitTests ArchUnit 테스트 목록
     * @param size 페이지 크기
     * @param hasNext 다음 페이지 존재 여부
     * @return ArchUnitTestSliceResult
     */
    public static ArchUnitTestSliceResult of(
            List<ArchUnitTestResult> archUnitTests, int size, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !archUnitTests.isEmpty()) {
            nextCursor = archUnitTests.get(archUnitTests.size() - 1).archUnitTestId();
        }
        return new ArchUnitTestSliceResult(archUnitTests, size, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return ArchUnit 테스트 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return archUnitTests.isEmpty();
    }

    /**
     * ArchUnit 테스트 개수 반환
     *
     * @return ArchUnit 테스트 개수
     */
    public int count() {
        return archUnitTests.size();
    }
}
