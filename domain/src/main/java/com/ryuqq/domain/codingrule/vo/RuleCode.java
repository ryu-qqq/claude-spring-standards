package com.ryuqq.domain.codingrule.vo;

/**
 * RuleCode - 규칙 코드 Value Object
 *
 * <p>예: DOM-001, APP-001, PERS-001
 *
 * @author ryu-qqq
 */
public record RuleCode(String value) {

    private static final int MAX_LENGTH = 20;

    public RuleCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RuleCode must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "RuleCode must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static RuleCode of(String value) {
        return new RuleCode(value);
    }
}
