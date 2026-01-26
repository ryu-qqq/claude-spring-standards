package com.ryuqq.domain.classtype.vo;

/**
 * ClassTypeName - 클래스 타입 이름 Value Object
 *
 * <p>클래스 타입의 표시 이름을 나타냅니다.
 *
 * @author ryu-qqq
 */
public record ClassTypeName(String value) {

    private static final int MAX_LENGTH = 100;

    public ClassTypeName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClassTypeName value must not be null or blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ClassTypeName value must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ClassTypeName of(String value) {
        return new ClassTypeName(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
