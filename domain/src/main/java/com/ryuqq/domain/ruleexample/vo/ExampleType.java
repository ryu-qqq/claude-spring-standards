package com.ryuqq.domain.ruleexample.vo;

/**
 * ExampleType - 예시 유형 Value Object
 *
 * @author ryu-qqq
 */
public enum ExampleType {
    GOOD("올바른 예시"),
    BAD("잘못된 예시");

    private final String description;

    ExampleType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isGood() {
        return this == GOOD;
    }

    public boolean isBad() {
        return this == BAD;
    }
}
