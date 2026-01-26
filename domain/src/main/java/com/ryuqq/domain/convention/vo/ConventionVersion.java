package com.ryuqq.domain.convention.vo;

/**
 * ConventionVersion - 컨벤션 버전 Value Object
 *
 * <p>Semantic Versioning 형식 (예: 1.0.0)
 *
 * @author ryu-qqq
 */
public record ConventionVersion(String value) {

    private static final int MAX_LENGTH = 20;
    private static final String DEFAULT_VERSION = "1.0.0";

    public ConventionVersion {
        if (value == null || value.isBlank()) {
            value = DEFAULT_VERSION;
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ConventionVersion must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ConventionVersion of(String value) {
        return new ConventionVersion(value);
    }

    public static ConventionVersion defaultVersion() {
        return new ConventionVersion(DEFAULT_VERSION);
    }
}
