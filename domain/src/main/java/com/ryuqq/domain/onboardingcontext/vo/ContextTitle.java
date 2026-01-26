package com.ryuqq.domain.onboardingcontext.vo;

/**
 * ContextTitle - 컨텍스트 제목 Value Object
 *
 * <p>온보딩 컨텍스트의 제목입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ContextTitle(String value) {

    public ContextTitle {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ContextTitle must not be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("ContextTitle must not exceed 100 characters");
        }
    }

    public static ContextTitle of(String value) {
        return new ContextTitle(value);
    }
}
