package com.ryuqq.domain.techstack.vo;

public enum TechStackStatus {

    /** 활성 상태 */
    ACTIVE("활성"),

    /** 비권장 상태 (사용은 가능하나 새 프로젝트에는 비권장) */
    DEPRECATED("비권장"),

    /** 보관 상태 (더 이상 사용하지 않음) */
    ARCHIVED("보관");

    private final String displayName;

    TechStackStatus(String displayName) {
        this.displayName = displayName;
    }

    /** 표시용 이름 반환 */
    public String displayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeprecated() {
        return this == DEPRECATED;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }
}
