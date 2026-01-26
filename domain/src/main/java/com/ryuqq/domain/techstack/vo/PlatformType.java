package com.ryuqq.domain.techstack.vo;

/**
 * PlatformType - 플랫폼 타입 열거형
 *
 * @author ryu-qqq
 */
public enum PlatformType {

    /** 백엔드 서버 */
    BACKEND("Backend"),

    /** 프론트엔드 */
    FRONTEND("Frontend"),

    /** 풀스택 */
    FULLSTACK("Full Stack"),

    /** SDK/라이브러리 */
    SDK("SDK");

    private final String displayName;

    PlatformType(String displayName) {
        this.displayName = displayName;
    }

    /** 표시용 이름 반환 */
    public String displayName() {
        return displayName;
    }
}
