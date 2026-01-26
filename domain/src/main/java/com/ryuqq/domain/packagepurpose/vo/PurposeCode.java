package com.ryuqq.domain.packagepurpose.vo;

/**
 * PurposeCode - 패키지 목적 코드 Value Object
 *
 * <p>예: AGGREGATE, VALUE_OBJECT, PORT_IN
 *
 * @author ryu-qqq
 */
public record PurposeCode(String value) {

    private static final int MAX_LENGTH = 50;

    public PurposeCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PurposeCode must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "PurposeCode must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static PurposeCode of(String value) {
        return new PurposeCode(value);
    }
}
