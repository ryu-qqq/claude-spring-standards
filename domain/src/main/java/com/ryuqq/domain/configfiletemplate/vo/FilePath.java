package com.ryuqq.domain.configfiletemplate.vo;

/**
 * FilePath - 파일 경로 Value Object
 *
 * <p>설정 파일이 생성될 경로입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record FilePath(String value) {

    public FilePath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FilePath must not be null or blank");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("FilePath must not exceed 200 characters");
        }
    }

    public static FilePath of(String value) {
        return new FilePath(value);
    }
}
