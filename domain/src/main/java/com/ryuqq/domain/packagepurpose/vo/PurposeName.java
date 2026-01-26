package com.ryuqq.domain.packagepurpose.vo;

/**
 * PurposeName - 패키지 목적 이름 Value Object
 *
 * <p>예: 애그리거트, 값 객체
 *
 * @author ryu-qqq
 */
public record PurposeName(String value) {

    private static final int MAX_LENGTH = 100;

    public PurposeName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PurposeName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "PurposeName must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static PurposeName of(String value) {
        return new PurposeName(value);
    }
}
