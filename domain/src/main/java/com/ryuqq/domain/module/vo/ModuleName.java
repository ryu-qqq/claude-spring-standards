package com.ryuqq.domain.module.vo;

/**
 * ModuleName - 모듈 이름 Value Object
 *
 * <p>예: domain, application, rest-api
 *
 * @author ryu-qqq
 */
public record ModuleName(String value) {

    private static final int MAX_LENGTH = 100;

    public ModuleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ModuleName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ModuleName must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ModuleName of(String value) {
        return new ModuleName(value);
    }
}
