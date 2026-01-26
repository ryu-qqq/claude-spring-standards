package com.ryuqq.domain.checklistitem.vo;

/**
 * AutomationTool - 자동화 도구 Value Object
 *
 * @author ryu-qqq
 */
public enum AutomationTool {
    ARCHUNIT("ArchUnit 아키텍처 테스트"),
    CHECKSTYLE("Checkstyle 코드 스타일 검사"),
    PMD("PMD 정적 분석"),
    SPOTBUGS("SpotBugs 버그 탐지"),
    SONARLINT("SonarLint 코드 품질"),
    JACOCO("JaCoCo 코드 커버리지"),
    CUSTOM("사용자 정의 검사 도구");

    private final String description;

    AutomationTool(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
