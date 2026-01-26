package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeApiResponse;
import java.util.List;

/**
 * PackagePurposeApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PackagePurposeApiResponseFixture {

    private PackagePurposeApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static PackagePurposeApiResponse valid() {
        return new PackagePurposeApiResponse(
                1L,
                1L,
                "AGGREGATE",
                "Aggregate Root",
                "DDD Aggregate Root 패키지",
                List.of("CLASS", "RECORD"),
                "^[A-Z][a-zA-Z0-9]*$",
                "Aggregate",
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 최소 필드만 */
    public static PackagePurposeApiResponse validMinimal() {
        return new PackagePurposeApiResponse(
                1L,
                1L,
                "VALUE_OBJECT",
                "Value Object",
                null,
                null,
                null,
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 커스텀 응답 생성 */
    public static PackagePurposeApiResponse custom(
            Long packagePurposeId,
            Long structureId,
            String code,
            String name,
            String description,
            List<String> defaultAllowedClassTypes,
            String defaultNamingPattern,
            String defaultNamingSuffix,
            String createdAt,
            String updatedAt) {
        return new PackagePurposeApiResponse(
                packagePurposeId,
                structureId,
                code,
                name,
                description,
                defaultAllowedClassTypes,
                defaultNamingPattern,
                defaultNamingSuffix,
                createdAt,
                updatedAt);
    }
}
