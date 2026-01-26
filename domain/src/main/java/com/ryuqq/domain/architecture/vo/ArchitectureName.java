package com.ryuqq.domain.architecture.vo;

/**
 * ArchitectureName - 아키텍처 이름 Value Object
 *
 * <p>예: hexagonal-multimodule
 *
 * @author ryu-qqq
 */
public record ArchitectureName(String value) {

    private static final int MAX_LENGTH = 100;

    public ArchitectureName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ArchitectureName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ArchitectureName must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ArchitectureName of(String value) {
        return new ArchitectureName(value);
    }
}
