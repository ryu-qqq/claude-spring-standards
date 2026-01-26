package com.ryuqq.domain.classtypecategory.vo;

/**
 * CategoryName - 카테고리 이름 Value Object
 *
 * <p>클래스 타입 카테고리의 표시 이름을 나타냅니다.
 *
 * @author ryu-qqq
 */
public record CategoryName(String value) {

    private static final int MAX_LENGTH = 100;

    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryName value must not be null or blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "CategoryName value must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static CategoryName of(String value) {
        return new CategoryName(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
