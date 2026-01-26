package com.ryuqq.domain.module.vo;

/**
 * GradlePath - Gradle 모듈 경로 Value Object
 *
 * <p>예: :domain, :adapter-in:rest-api
 *
 * @author ryu-qqq
 */
public record GradlePath(String value) {

    private static final int MAX_LENGTH = 200;

    public GradlePath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("GradlePath must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "GradlePath must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static GradlePath of(String value) {
        return new GradlePath(value);
    }
}
