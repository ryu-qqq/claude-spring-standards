package com.ryuqq.domain.ruleexample.vo;

/**
 * ExampleLanguage - 예시 코드 언어 Value Object
 *
 * @author ryu-qqq
 */
public enum ExampleLanguage {
    JAVA("Java"),
    KOTLIN("Kotlin"),
    SQL("SQL"),
    YAML("YAML"),
    JSON("JSON"),
    GRADLE("Gradle"),
    XML("XML");

    private final String displayName;

    ExampleLanguage(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
