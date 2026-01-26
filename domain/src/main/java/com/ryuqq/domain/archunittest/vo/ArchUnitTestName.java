package com.ryuqq.domain.archunittest.vo;

import java.util.Objects;

/**
 * ArchUnitTestName - ArchUnit 테스트 이름 Value Object
 *
 * <p>테스트의 이름을 나타내는 불변 객체입니다.
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public record ArchUnitTestName(String value) {

    private static final int MAX_LENGTH = 255;

    public ArchUnitTestName {
        Objects.requireNonNull(value, "ArchUnitTestName must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ArchUnitTestName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("ArchUnitTestName must not exceed %d characters", MAX_LENGTH));
        }
    }

    public static ArchUnitTestName of(String value) {
        return new ArchUnitTestName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
