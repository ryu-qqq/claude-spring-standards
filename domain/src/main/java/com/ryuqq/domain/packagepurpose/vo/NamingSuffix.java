package com.ryuqq.domain.packagepurpose.vo;

/**
 * NamingSuffix - 네이밍 접미사 Value Object
 *
 * <p>예: Service, Repository
 *
 * @author ryu-qqq
 */
public record NamingSuffix(String value) {

    private static final int MAX_LENGTH = 50;

    public NamingSuffix {
        if (value != null && value.isBlank()) {
            value = null;
        }
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "NamingSuffix must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static NamingSuffix of(String value) {
        return new NamingSuffix(value);
    }

    public static NamingSuffix empty() {
        return new NamingSuffix(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
