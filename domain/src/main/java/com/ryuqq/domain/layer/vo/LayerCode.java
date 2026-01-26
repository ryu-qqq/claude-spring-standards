package com.ryuqq.domain.layer.vo;

/**
 * LayerCode - 레이어 코드 Value Object
 *
 * <p>예: DOMAIN, APPLICATION, PERSISTENCE, REST_API
 *
 * @author ryu-qqq
 */
public record LayerCode(String value) {

    private static final int MAX_LENGTH = 50;

    public LayerCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LayerCode must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "LayerCode must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static LayerCode of(String value) {
        return new LayerCode(value);
    }
}
