package com.ryuqq.domain.module.vo;

/**
 * ModulePath - 모듈 파일시스템 경로 Value Object
 *
 * <p>빌드 시스템에 관계없이 프로젝트 내 모듈의 상대 경로를 나타냅니다.
 *
 * <p>예:
 *
 * <ul>
 *   <li>Java/Gradle: "adapter-in/rest-api"
 *   <li>Frontend/npm: "packages/web"
 *   <li>Terraform: "modules/vpc"
 * </ul>
 *
 * @author ryu-qqq
 */
public record ModulePath(String value) {

    private static final int MAX_LENGTH = 500;

    public ModulePath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ModulePath must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ModulePath must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ModulePath of(String value) {
        return new ModulePath(value);
    }

    /**
     * 경로 깊이 반환
     *
     * @return 슬래시 기준 깊이
     */
    public int depth() {
        return (int) value.chars().filter(c -> c == '/').count() + 1;
    }

    /**
     * 부모 경로 반환
     *
     * @return 부모 경로 (루트인 경우 null)
     */
    public ModulePath parent() {
        int lastSlash = value.lastIndexOf('/');
        if (lastSlash <= 0) {
            return null;
        }
        return new ModulePath(value.substring(0, lastSlash));
    }

    /**
     * 모듈 이름만 반환 (경로의 마지막 부분)
     *
     * @return 모듈 이름
     */
    public String moduleName() {
        int lastSlash = value.lastIndexOf('/');
        return lastSlash >= 0 ? value.substring(lastSlash + 1) : value;
    }
}
