package com.ryuqq.domain.zerotolerance.vo;

/**
 * DetectionType - 탐지 방식 Value Object
 *
 * <p>Zero Tolerance 규칙 위반을 탐지하는 방식을 정의합니다.
 *
 * @author ryu-qqq
 */
public enum DetectionType {

    /** 정규식 기반 탐지 */
    REGEX("정규식 기반 탐지"),

    /** AST(Abstract Syntax Tree) 기반 탐지 */
    AST("AST 기반 탐지"),

    /** ArchUnit 기반 아키텍처 테스트 탐지 */
    ARCHUNIT("ArchUnit 기반 탐지");

    private final String description;

    DetectionType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /**
     * 문자열로부터 DetectionType 생성
     *
     * @param value 문자열 값
     * @return DetectionType
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static DetectionType of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DetectionType value must not be blank");
        }
        try {
            return DetectionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DetectionType: " + value);
        }
    }
}
