package com.ryuqq.domain.member;

import java.util.regex.Pattern;

public record Email(String value) {

    // RFC 5322 간소화 버전 정규식
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final int MAX_LENGTH = 320;

    public Email {
        validate(value);
    }

    private static void validate(String value) {
        validateNotEmpty(value);
        validateLength(value);
        validateFormat(value);
    }

    private static void validateNotEmpty(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidEmailFormatException("이메일은 필수입니다.");
        }
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new InvalidEmailFormatException("이메일은 320자를 초과할 수 없습니다.");
        }
    }

    private static void validateFormat(String value) {
        if (!value.contains("@")) {
            throw new InvalidEmailFormatException("유효한 이메일 형식이 아닙니다.");
        }

        String[] parts = value.split("@");
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new InvalidEmailFormatException("유효한 이메일 형식이 아닙니다.");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailFormatException("유효한 이메일 형식이 아닙니다.");
        }
    }
}
