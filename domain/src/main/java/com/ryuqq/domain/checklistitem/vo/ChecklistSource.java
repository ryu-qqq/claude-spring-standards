package com.ryuqq.domain.checklistitem.vo;

/**
 * ChecklistSource - 체크리스트 출처 Value Object
 *
 * @author ryu-qqq
 */
public enum ChecklistSource {
    MANUAL("수동 입력"),
    AGENT_FEEDBACK("에이전트 피드백에서 승격");

    private final String description;

    ChecklistSource(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
