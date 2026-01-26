package com.ryuqq.domain.module.vo;

/**
 * BuildIdentifier - 빌드 시스템 식별자 Value Object
 *
 * <p>각 빌드 시스템에서 사용하는 모듈 식별자를 나타냅니다.
 *
 * <p>예:
 *
 * <ul>
 *   <li>Gradle: ":adapter-in:rest-api"
 *   <li>Maven: "com.ryuqq:rest-api"
 *   <li>npm: "@company/web"
 *   <li>Terraform: "module.vpc"
 * </ul>
 *
 * @author ryu-qqq
 */
public record BuildIdentifier(String value) {

    private static final int MAX_LENGTH = 500;

    public BuildIdentifier {
        // nullable 허용 - 빌드 식별자가 없는 경우도 있음
        if (value != null) {
            if (value.isBlank()) {
                throw new IllegalArgumentException(
                        "BuildIdentifier must not be blank (use null for no identifier)");
            }
            if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "BuildIdentifier must not exceed " + MAX_LENGTH + " characters");
            }
        }
    }

    public static BuildIdentifier of(String value) {
        return new BuildIdentifier(value);
    }

    public static BuildIdentifier empty() {
        return new BuildIdentifier(null);
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isPresent() {
        return value != null;
    }
}
