package com.ryuqq.domain.configfiletemplate.vo;

/**
 * FileName - 파일명 Value Object
 *
 * <p>설정 파일의 이름입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record FileName(String value) {

    public FileName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FileName must not be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("FileName must not exceed 100 characters");
        }
    }

    public static FileName of(String value) {
        return new FileName(value);
    }
}
