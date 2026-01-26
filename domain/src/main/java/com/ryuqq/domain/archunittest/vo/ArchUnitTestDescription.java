package com.ryuqq.domain.archunittest.vo;

import java.util.Objects;

/**
 * ArchUnitTestDescription - ArchUnit 테스트 설명 Value Object
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public record ArchUnitTestDescription(String value) {

    private static final int MAX_LENGTH = 2000;

    public ArchUnitTestDescription {
        Objects.requireNonNull(value, "ArchUnitTestDescription must not be null");
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(
                            "ArchUnitTestDescription must not exceed %d characters", MAX_LENGTH));
        }
    }

    public static ArchUnitTestDescription of(String value) {
        return new ArchUnitTestDescription(value);
    }

    public static ArchUnitTestDescription empty() {
        return new ArchUnitTestDescription("");
    }

    public boolean isEmpty() {
        return value.isBlank();
    }

    @Override
    public String toString() {
        return value;
    }
}
