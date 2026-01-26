package com.ryuqq.domain.classtype.vo;

/**
 * ClassTypeCode - 클래스 타입 코드 Value Object
 *
 * <p>클래스 타입의 고유 코드를 나타냅니다.
 *
 * @author ryu-qqq
 */
public record ClassTypeCode(String value) {

    private static final int MAX_LENGTH = 50;

    public ClassTypeCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClassTypeCode value must not be null or blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ClassTypeCode value must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ClassTypeCode of(String value) {
        return new ClassTypeCode(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
