package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeIdApiResponse;

/**
 * PackagePurposeIdApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PackagePurposeIdApiResponseFixture {

    private PackagePurposeIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static PackagePurposeIdApiResponse valid() {
        return PackagePurposeIdApiResponse.of(1L);
    }

    /** 커스텀 응답 생성 */
    public static PackagePurposeIdApiResponse custom(Long packagePurposeId) {
        return PackagePurposeIdApiResponse.of(packagePurposeId);
    }
}
