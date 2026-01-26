package com.ryuqq.domain.checklistitem.vo;

/**
 * CheckType - 체크 유형 Value Object
 *
 * @author ryu-qqq
 */
public enum CheckType {
    AUTOMATED("자동 검사"),
    MANUAL("수동 검사"),
    SEMI_AUTO("반자동 검사");

    private final String description;

    CheckType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isAutomated() {
        return this == AUTOMATED;
    }

    public boolean requiresManualCheck() {
        return this == MANUAL || this == SEMI_AUTO;
    }
}
