package com.ryuqq.domain.packagestructure.vo;

/**
 * PathPattern - 패키지 경로 패턴 Value Object
 *
 * <p>예: {base}.domain.{bc}.aggregate
 *
 * @author ryu-qqq
 */
public record PathPattern(String value) {

    private static final int MAX_LENGTH = 300;

    public PathPattern {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PathPattern must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "PathPattern must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static PathPattern of(String value) {
        return new PathPattern(value);
    }
}
