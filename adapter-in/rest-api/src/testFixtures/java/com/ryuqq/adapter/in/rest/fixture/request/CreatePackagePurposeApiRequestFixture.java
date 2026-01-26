package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.CreatePackagePurposeApiRequest;
import java.util.List;

/**
 * CreatePackagePurposeApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreatePackagePurposeApiRequestFixture {

    private CreatePackagePurposeApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static CreatePackagePurposeApiRequest valid() {
        return new CreatePackagePurposeApiRequest(
                1L,
                "AGGREGATE",
                "Aggregate Root",
                "DDD Aggregate Root 패키지",
                List.of("CLASS", "RECORD"),
                "^[A-Z][a-zA-Z0-9]*$",
                "Aggregate");
    }

    /** 정상 요청 - 최소 필수 필드만 */
    public static CreatePackagePurposeApiRequest validMinimal() {
        return new CreatePackagePurposeApiRequest(
                1L, "VALUE_OBJECT", "Value Object", null, null, null, null);
    }

    /** 잘못된 요청 - structureId 누락 (null) */
    public static CreatePackagePurposeApiRequest invalidWithNullStructureId() {
        return new CreatePackagePurposeApiRequest(
                null, "AGGREGATE", "Aggregate Root", null, null, null, null);
    }

    /** 잘못된 요청 - code 누락 (빈 문자열) */
    public static CreatePackagePurposeApiRequest invalidWithBlankCode() {
        return new CreatePackagePurposeApiRequest(1L, "", "Aggregate Root", null, null, null, null);
    }

    /** 잘못된 요청 - code 길이 초과 (50자 초과) */
    public static CreatePackagePurposeApiRequest invalidWithLongCode() {
        String longCode = "A".repeat(51);
        return new CreatePackagePurposeApiRequest(
                1L, longCode, "Aggregate Root", null, null, null, null);
    }

    /** 잘못된 요청 - name 누락 (빈 문자열) */
    public static CreatePackagePurposeApiRequest invalidWithBlankName() {
        return new CreatePackagePurposeApiRequest(1L, "AGGREGATE", "", null, null, null, null);
    }

    /** 잘못된 요청 - name 길이 초과 (100자 초과) */
    public static CreatePackagePurposeApiRequest invalidWithLongName() {
        String longName = "A".repeat(101);
        return new CreatePackagePurposeApiRequest(
                1L, "AGGREGATE", longName, null, null, null, null);
    }

    /** 잘못된 요청 - description 길이 초과 (2000자 초과) */
    public static CreatePackagePurposeApiRequest invalidWithLongDescription() {
        String longDescription = "A".repeat(2001);
        return new CreatePackagePurposeApiRequest(
                1L, "AGGREGATE", "Aggregate Root", longDescription, null, null, null);
    }

    /** 잘못된 요청 - defaultNamingPattern 길이 초과 (200자 초과) */
    public static CreatePackagePurposeApiRequest invalidWithLongNamingPattern() {
        String longPattern = "A".repeat(201);
        return new CreatePackagePurposeApiRequest(
                1L, "AGGREGATE", "Aggregate Root", null, null, longPattern, null);
    }

    /** 잘못된 요청 - defaultNamingSuffix 길이 초과 (50자 초과) */
    public static CreatePackagePurposeApiRequest invalidWithLongNamingSuffix() {
        String longSuffix = "A".repeat(51);
        return new CreatePackagePurposeApiRequest(
                1L, "AGGREGATE", "Aggregate Root", null, null, null, longSuffix);
    }

    /** 커스텀 요청 생성 */
    public static CreatePackagePurposeApiRequest custom(
            Long structureId,
            String code,
            String name,
            String description,
            List<String> defaultAllowedClassTypes,
            String defaultNamingPattern,
            String defaultNamingSuffix) {
        return new CreatePackagePurposeApiRequest(
                structureId,
                code,
                name,
                description,
                defaultAllowedClassTypes,
                defaultNamingPattern,
                defaultNamingSuffix);
    }
}
