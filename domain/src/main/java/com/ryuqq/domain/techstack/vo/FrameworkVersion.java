package com.ryuqq.domain.techstack.vo;

/**
 * FrameworkVersion - 프레임워크 버전 Value Object
 *
 * <p>예: 3.5.0, 2.7.x
 *
 * @author ryu-qqq
 */
public record FrameworkVersion(String value) {

    private static final int MAX_LENGTH = 20;

    public FrameworkVersion {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FrameworkVersion must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "FrameworkVersion must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static FrameworkVersion of(String value) {
        return new FrameworkVersion(value);
    }
}
