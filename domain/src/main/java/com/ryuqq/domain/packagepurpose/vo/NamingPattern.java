package com.ryuqq.domain.packagepurpose.vo;

/**
 * NamingPattern - 네이밍 패턴 Value Object (정규식)
 *
 * @author ryu-qqq
 */
public record NamingPattern(String value) {

    private static final int MAX_LENGTH = 200;

    public NamingPattern {
        if (value != null && value.isBlank()) {
            value = null;
        }
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "NamingPattern must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static NamingPattern of(String value) {
        return new NamingPattern(value);
    }

    public static NamingPattern empty() {
        return new NamingPattern(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
