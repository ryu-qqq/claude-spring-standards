package com.ryuqq.domain.checklistitem.vo;

/**
 * AutomationRuleId - 자동화 도구 규칙 ID Value Object
 *
 * <p>자동화 도구에서 사용하는 규칙 식별자
 *
 * @author ryu-qqq
 */
public record AutomationRuleId(String value) {

    private static final int MAX_LENGTH = 100;

    public AutomationRuleId {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "AutomationRuleId must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static AutomationRuleId of(String value) {
        return new AutomationRuleId(value);
    }

    public static AutomationRuleId empty() {
        return new AutomationRuleId(null);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
