package com.ryuqq.domain.ruleexample.vo;

/**
 * ExampleSource - 예시 출처 Value Object
 *
 * @author ryu-qqq
 */
public enum ExampleSource {
    MANUAL("수동 입력"),
    AGENT_FEEDBACK("에이전트 피드백에서 승격");

    private final String description;

    ExampleSource(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
