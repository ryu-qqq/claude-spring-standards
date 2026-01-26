package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.layer.dto.response.LayerApiResponse;

/**
 * LayerApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class LayerApiResponseFixture {

    private static final String DEFAULT_CREATED_AT = "2024-01-01T00:00:00Z";
    private static final String DEFAULT_UPDATED_AT = "2024-01-01T00:00:00Z";

    private LayerApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static LayerApiResponse valid() {
        return new LayerApiResponse(
                1L,
                1L,
                "DOMAIN",
                "Domain Layer",
                "Domain Layer Description",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static LayerApiResponse withId(Long id) {
        return new LayerApiResponse(
                id,
                1L,
                "DOMAIN",
                "Domain Layer",
                "Domain Layer Description",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static LayerApiResponse application() {
        return new LayerApiResponse(
                2L,
                1L,
                "APPLICATION",
                "Application Layer",
                "Application Layer Description",
                2,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static LayerApiResponse persistence() {
        return new LayerApiResponse(
                3L,
                1L,
                "PERSISTENCE",
                "Persistence Layer",
                "Persistence Layer Description",
                3,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static LayerApiResponse restApi() {
        return new LayerApiResponse(
                4L,
                1L,
                "REST_API",
                "REST API Layer",
                "REST API Layer Description",
                4,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }
}
