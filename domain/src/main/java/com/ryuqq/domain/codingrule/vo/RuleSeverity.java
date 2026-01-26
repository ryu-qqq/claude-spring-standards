package com.ryuqq.domain.codingrule.vo;

/**
 * RuleSeverity - 규칙 심각도 Value Object
 *
 * @author ryu-qqq
 */
public enum RuleSeverity {
    BLOCKER("치명적 - 빌드 차단"),
    CRITICAL("중대 - 반드시 수정 필요"),
    MAJOR("주요 - 수정 권장"),
    MINOR("경미 - 선택적 수정"),
    INFO("정보 - 참고용");

    private final String description;

    RuleSeverity(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isBlocker() {
        return this == BLOCKER;
    }

    public boolean isHighPriority() {
        return this == BLOCKER || this == CRITICAL;
    }
}
