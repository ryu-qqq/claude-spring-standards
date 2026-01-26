package com.ryuqq.domain.onboardingcontext.vo;

/**
 * Priority - 우선순위 Value Object
 *
 * <p>온보딩 시 표시 순서입니다. 낮을수록 먼저 표시됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record Priority(Integer value) {

    public static Priority of(Integer value) {
        return new Priority(value != null ? value : 0);
    }

    public static Priority defaultPriority() {
        return new Priority(0);
    }
}
