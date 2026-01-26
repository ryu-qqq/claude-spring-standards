package com.ryuqq.domain.techstack.vo;

/**
 * FrameworkType - 프레임워크 타입 열거형
 *
 * @author ryu-qqq
 */
public enum FrameworkType {
    SPRING_BOOT("Spring Boot"),
    SPRING_CLOUD("Spring Cloud"),
    SPRING_WEBFLUX("Spring WebFlux"),
    KTOR("Ktor"),
    NESTJS("NestJS"),
    EXPRESS("Express"),
    FASTAPI("FastAPI"),
    GIN("Gin");

    private final String displayName;

    FrameworkType(String displayName) {
        this.displayName = displayName;
    }

    /** 표시용 이름 반환 */
    public String displayName() {
        return displayName;
    }
}
