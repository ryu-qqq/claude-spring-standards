package com.ryuqq.domain.ruleexample.vo;

/**
 * ExampleCode - 예시 코드 Value Object
 *
 * @author ryu-qqq
 */
public record ExampleCode(String value) {

    public ExampleCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExampleCode must not be blank");
        }
    }

    public static ExampleCode of(String value) {
        return new ExampleCode(value);
    }
}
