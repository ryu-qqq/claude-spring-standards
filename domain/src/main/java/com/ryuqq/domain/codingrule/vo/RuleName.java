package com.ryuqq.domain.codingrule.vo;

/**
 * RuleName - 규칙 이름 Value Object
 *
 * @author ryu-qqq
 */
public record RuleName(String value) {

    private static final int MAX_LENGTH = 200;

    public RuleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RuleName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "RuleName must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static RuleName of(String value) {
        return new RuleName(value);
    }
}
