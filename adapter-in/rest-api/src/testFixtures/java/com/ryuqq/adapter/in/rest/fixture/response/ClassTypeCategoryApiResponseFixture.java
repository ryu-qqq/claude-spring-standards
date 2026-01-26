package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.classtypecategory.dto.response.ClassTypeCategoryApiResponse;

/**
 * ClassTypeCategoryApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ClassTypeCategoryApiResponseFixture {

    private static final String DEFAULT_CREATED_AT = "2024-01-01T00:00:00Z";
    private static final String DEFAULT_UPDATED_AT = "2024-01-01T00:00:00Z";

    private ClassTypeCategoryApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ClassTypeCategoryApiResponse valid() {
        return new ClassTypeCategoryApiResponse(
                1L,
                1L,
                "DOMAIN_TYPES",
                "도메인 타입",
                "도메인 레이어 클래스 타입",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeCategoryApiResponse withId(Long id) {
        return new ClassTypeCategoryApiResponse(
                id,
                1L,
                "DOMAIN_TYPES",
                "도메인 타입",
                "도메인 레이어 클래스 타입",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeCategoryApiResponse applicationTypes() {
        return new ClassTypeCategoryApiResponse(
                2L,
                1L,
                "APPLICATION_TYPES",
                "애플리케이션 타입",
                "애플리케이션 레이어 클래스 타입",
                2,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeCategoryApiResponse adapterTypes() {
        return new ClassTypeCategoryApiResponse(
                3L,
                1L,
                "ADAPTER_TYPES",
                "어댑터 타입",
                "어댑터 레이어 클래스 타입",
                3,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeCategoryApiResponse infrastructureTypes() {
        return new ClassTypeCategoryApiResponse(
                4L,
                1L,
                "INFRASTRUCTURE_TYPES",
                "인프라 타입",
                "인프라 레이어 클래스 타입",
                4,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }
}
