package com.ryuqq.domain.techstack.vo;

/**
 * BuildToolType - 빌드 도구 타입 열거형
 *
 * @author ryu-qqq
 */
public enum BuildToolType {
    GRADLE("Gradle"),
    MAVEN("Maven"),
    NPM("npm"),
    YARN("Yarn"),
    PNPM("pnpm"),
    PIP("pip"),
    POETRY("Poetry"),
    GO_MOD("Go Modules"),
    CARGO("Cargo");

    private final String displayName;

    BuildToolType(String displayName) {
        this.displayName = displayName;
    }

    /** 표시용 이름 반환 */
    public String displayName() {
        return displayName;
    }
}
