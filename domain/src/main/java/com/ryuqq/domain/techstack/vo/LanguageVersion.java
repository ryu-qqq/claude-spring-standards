package com.ryuqq.domain.techstack.vo;

/**
 * LanguageVersion - 언어 버전 Value Object
 *
 * <p>예: 21, 17, 5.0
 *
 * @author ryu-qqq
 */
public record LanguageVersion(String value) {

    private static final int MAX_LENGTH = 20;

    public LanguageVersion {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LanguageVersion must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "LanguageVersion must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static LanguageVersion of(String value) {
        return new LanguageVersion(value);
    }
}
