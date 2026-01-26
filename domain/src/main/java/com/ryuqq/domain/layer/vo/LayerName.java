package com.ryuqq.domain.layer.vo;

/**
 * LayerName - 레이어 이름 Value Object
 *
 * <p>예: 도메인 레이어, 애플리케이션 레이어
 *
 * @author ryu-qqq
 */
public record LayerName(String value) {

    private static final int MAX_LENGTH = 100;

    public LayerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LayerName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "LayerName must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static LayerName of(String value) {
        return new LayerName(value);
    }
}
