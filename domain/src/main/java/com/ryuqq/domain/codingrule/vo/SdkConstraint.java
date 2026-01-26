package com.ryuqq.domain.codingrule.vo;

/**
 * SdkConstraint - SDK 제약 조건 Value Object
 *
 * <p>규칙이 특정 SDK 버전에만 적용될 때 사용합니다.
 *
 * @author ryu-qqq
 */
public record SdkConstraint(String artifact, String minVersion, String maxVersion) {

    public SdkConstraint {
        // maxVersion can be null meaning "latest"
    }

    public static SdkConstraint of(String artifact, String minVersion, String maxVersion) {
        return new SdkConstraint(artifact, minVersion, maxVersion);
    }

    public static SdkConstraint empty() {
        return new SdkConstraint(null, null, null);
    }

    public boolean isEmpty() {
        return artifact == null || artifact.isBlank();
    }

    public boolean hasMinVersion() {
        return minVersion != null && !minVersion.isBlank();
    }

    public boolean hasMaxVersion() {
        return maxVersion != null && !maxVersion.isBlank();
    }
}
