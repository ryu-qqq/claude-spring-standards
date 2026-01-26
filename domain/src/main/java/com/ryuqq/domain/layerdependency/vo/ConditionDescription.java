package com.ryuqq.domain.layerdependency.vo;

/**
 * ConditionDescription - 조건부 의존성 설명 Value Object
 *
 * <p>CONDITIONAL 의존성인 경우의 조건 설명입니다.
 *
 * @author ryu-qqq
 */
public record ConditionDescription(String value) {

    private static final int MAX_LENGTH = 2000;

    public ConditionDescription {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ConditionDescription must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ConditionDescription of(String value) {
        return new ConditionDescription(value);
    }

    public static ConditionDescription empty() {
        return new ConditionDescription(null);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
