package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.UpdatePackagePurposeApiRequest;

/**
 * UpdatePackagePurposeApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdatePackagePurposeApiRequestFixture {

    private UpdatePackagePurposeApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static UpdatePackagePurposeApiRequest valid() {
        return new UpdatePackagePurposeApiRequest(
                "AGGREGATE", "Aggregate Root", "DDD Aggregate Root 패키지");
    }

    /** 정상 요청 - 최소 필수 필드만 */
    public static UpdatePackagePurposeApiRequest validMinimal() {
        return new UpdatePackagePurposeApiRequest("VALUE_OBJECT", "Value Object", null);
    }

    /** 잘못된 요청 - code 누락 (빈 문자열) */
    public static UpdatePackagePurposeApiRequest invalidWithBlankCode() {
        return new UpdatePackagePurposeApiRequest("", "Aggregate Root", null);
    }

    /** 잘못된 요청 - code 길이 초과 (50자 초과) */
    public static UpdatePackagePurposeApiRequest invalidWithLongCode() {
        String longCode = "A".repeat(51);
        return new UpdatePackagePurposeApiRequest(longCode, "Aggregate Root", null);
    }

    /** 잘못된 요청 - name 누락 (빈 문자열) */
    public static UpdatePackagePurposeApiRequest invalidWithBlankName() {
        return new UpdatePackagePurposeApiRequest("AGGREGATE", "", null);
    }

    /** 잘못된 요청 - name 길이 초과 (100자 초과) */
    public static UpdatePackagePurposeApiRequest invalidWithLongName() {
        String longName = "A".repeat(101);
        return new UpdatePackagePurposeApiRequest("AGGREGATE", longName, null);
    }

    /** 잘못된 요청 - description 길이 초과 (2000자 초과) */
    public static UpdatePackagePurposeApiRequest invalidWithLongDescription() {
        String longDescription = "A".repeat(2001);
        return new UpdatePackagePurposeApiRequest("AGGREGATE", "Aggregate Root", longDescription);
    }

    /** 커스텀 요청 생성 */
    public static UpdatePackagePurposeApiRequest custom(
            String code, String name, String description) {
        return new UpdatePackagePurposeApiRequest(code, name, description);
    }
}
