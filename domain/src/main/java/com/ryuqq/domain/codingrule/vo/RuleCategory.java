package com.ryuqq.domain.codingrule.vo;

/**
 * RuleCategory - 규칙 카테고리 Value Object
 *
 * @author ryu-qqq
 */
public enum RuleCategory {
    ANNOTATION("어노테이션 관련"),
    BEHAVIOR("동작/행위 관련"),
    STRUCTURE("구조/패턴 관련"),
    DEPENDENCY("의존성 관련"),
    NAMING("네이밍 관련"),
    DOCUMENTATION("문서화 관련"),
    TESTING("테스트 관련"),
    PERFORMANCE("성능 관련"),
    SECURITY("보안 관련"),
    LOCATION("위치 관련"),
    STYLE("스타일 관련");

    private final String description;

    RuleCategory(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
