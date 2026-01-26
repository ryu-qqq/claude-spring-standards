package com.ryuqq.domain.packagepurpose.fixture;

import com.ryuqq.domain.packagepurpose.exception.PackagePurposeDuplicateCodeException;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeNotFoundException;

/**
 * PackagePurpose Exception Test Fixture
 *
 * <p>Domain 예외 테스트에서 사용하는 Exception 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PackagePurposeExceptionFixture {

    private PackagePurposeExceptionFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** PackagePurposeNotFoundException - 기본 케이스 */
    public static PackagePurposeNotFoundException notFound() {
        return new PackagePurposeNotFoundException(1L);
    }

    /** PackagePurposeNotFoundException - 커스텀 ID */
    public static PackagePurposeNotFoundException notFound(Long packagePurposeId) {
        return new PackagePurposeNotFoundException(packagePurposeId);
    }

    /** PackagePurposeDuplicateCodeException - 기본 케이스 */
    public static PackagePurposeDuplicateCodeException duplicateCode() {
        return new PackagePurposeDuplicateCodeException(1L, "AGGREGATE");
    }

    /** PackagePurposeDuplicateCodeException - 커스텀 값 */
    public static PackagePurposeDuplicateCodeException duplicateCode(
            Long structureId, String code) {
        return new PackagePurposeDuplicateCodeException(structureId, code);
    }
}
