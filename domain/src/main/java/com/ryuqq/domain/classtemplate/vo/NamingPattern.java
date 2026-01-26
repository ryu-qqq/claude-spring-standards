package com.ryuqq.domain.classtemplate.vo;

/**
 * NamingPattern - 클래스명 네이밍 패턴 (정규식)
 *
 * <p>클래스 이름이 따라야 할 패턴을 정의합니다.
 *
 * @author ryu-qqq
 */
public record NamingPattern(String value) {

    private static final int MAX_LENGTH = 200;

    public NamingPattern {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "NamingPattern must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static NamingPattern of(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new NamingPattern(value);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
