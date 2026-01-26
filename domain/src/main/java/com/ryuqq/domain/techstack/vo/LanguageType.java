package com.ryuqq.domain.techstack.vo;

/**
 * LanguageType - 프로그래밍 언어 타입 열거형
 *
 * @author ryu-qqq
 */
public enum LanguageType {
    JAVA("Java"),
    KOTLIN("Kotlin"),
    TYPESCRIPT("TypeScript"),
    JAVASCRIPT("JavaScript"),
    PYTHON("Python"),
    GO("Go"),
    RUST("Rust");

    private final String displayName;

    LanguageType(String displayName) {
        this.displayName = displayName;
    }

    /** 표시용 이름 반환 */
    public String displayName() {
        return displayName;
    }
}
