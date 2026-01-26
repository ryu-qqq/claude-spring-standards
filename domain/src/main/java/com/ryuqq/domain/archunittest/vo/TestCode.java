package com.ryuqq.domain.archunittest.vo;

import java.util.Objects;

/**
 * TestCode - ArchUnit 테스트 코드 Value Object
 *
 * <p>실제 ArchUnit 테스트 코드를 저장하는 불변 객체입니다.
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public record TestCode(String value) {

    private static final int MAX_LENGTH = 10000;

    public TestCode {
        Objects.requireNonNull(value, "TestCode must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("TestCode must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("TestCode must not exceed %d characters", MAX_LENGTH));
        }
    }

    public static TestCode of(String value) {
        return new TestCode(value);
    }

    /**
     * 코드에 특정 패턴이 포함되어 있는지 확인
     *
     * @param pattern 검색할 패턴
     * @return 포함되어 있으면 true
     */
    public boolean contains(String pattern) {
        return value.contains(pattern);
    }

    /**
     * 코드의 줄 수 반환
     *
     * @return 줄 수
     */
    public int lineCount() {
        return value.split("\n").length;
    }

    @Override
    public String toString() {
        return value;
    }
}
