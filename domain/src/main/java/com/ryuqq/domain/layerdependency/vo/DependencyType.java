package com.ryuqq.domain.layerdependency.vo;

/**
 * DependencyType - 의존성 유형 Value Object
 *
 * <p>레이어 간 의존성 허용 여부를 정의합니다.
 *
 * @author ryu-qqq
 */
public enum DependencyType {
    ALLOWED("허용됨"),
    FORBIDDEN("금지됨"),
    CONDITIONAL("조건부 허용");

    private final String description;

    DependencyType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
