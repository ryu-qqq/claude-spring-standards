package com.ryuqq.domain.techstack.vo;

/**
 * BuildConfigFile - 빌드 설정 파일 Value Object
 *
 * <p>예: build.gradle, pom.xml, package.json
 *
 * @author ryu-qqq
 */
public record BuildConfigFile(String value) {

    private static final int MAX_LENGTH = 100;

    public BuildConfigFile {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BuildConfigFile must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "BuildConfigFile must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static BuildConfigFile of(String value) {
        return new BuildConfigFile(value);
    }
}
