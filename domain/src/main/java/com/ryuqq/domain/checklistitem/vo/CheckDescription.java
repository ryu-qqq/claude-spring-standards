package com.ryuqq.domain.checklistitem.vo;

/**
 * CheckDescription - 체크 항목 설명 Value Object
 *
 * @author ryu-qqq
 */
public record CheckDescription(String value) {

    private static final int MAX_LENGTH = 500;

    public CheckDescription {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CheckDescription must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "CheckDescription must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static CheckDescription of(String value) {
        return new CheckDescription(value);
    }
}
