package com.ryuqq.domain.techstack.vo;

public record TechStackName(String value) {

    private static final int MAX_LENGTH = 100;

    /** Compact Constructor - 유효성 검증 */
    public TechStackName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TechStackName must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "TechStackName must not exceed " + MAX_LENGTH + " characters");
        }
    }

    /** 정적 팩토리 메서드 */
    public static TechStackName of(String value) {
        return new TechStackName(value);
    }
}
