package com.ryuqq.domain.onboardingcontext.vo;

/**
 * ContextContent - 컨텍스트 내용 Value Object
 *
 * <p>온보딩 컨텍스트의 내용입니다. Markdown을 지원합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ContextContent(String value) {

    public ContextContent {
        if (value == null) {
            throw new IllegalArgumentException("ContextContent must not be null");
        }
    }

    public static ContextContent of(String value) {
        return new ContextContent(value);
    }

    public static ContextContent empty() {
        return new ContextContent("");
    }
}
