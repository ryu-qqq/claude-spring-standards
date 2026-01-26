package com.ryuqq.domain.classtypecategory.vo;

/**
 * CategoryCode - 카테고리 코드 Value Object
 *
 * <p>클래스 타입 카테고리의 고유 코드를 나타냅니다.
 *
 * @author ryu-qqq
 */
public record CategoryCode(String value) {

    private static final int MAX_LENGTH = 50;

    public CategoryCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryCode value must not be null or blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "CategoryCode value must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static CategoryCode of(String value) {
        return new CategoryCode(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
