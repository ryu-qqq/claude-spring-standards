package com.ryuqq.domain.architecture.vo;

/**
 * PatternDescription - 패턴 설명 Value Object
 *
 * @author ryu-qqq
 */
public record PatternDescription(String value) {

    public PatternDescription {
        // nullable 허용
        if (value != null && value.isBlank()) {
            value = null;
        }
    }

    public static PatternDescription of(String value) {
        return new PatternDescription(value);
    }

    public static PatternDescription empty() {
        return new PatternDescription(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
