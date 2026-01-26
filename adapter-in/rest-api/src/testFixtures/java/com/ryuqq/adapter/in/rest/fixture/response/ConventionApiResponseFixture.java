package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.convention.dto.response.ConventionApiResponse;

/**
 * ConventionApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ConventionApiResponseFixture {

    private ConventionApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ConventionApiResponse valid() {
        return new ConventionApiResponse(
                1L,
                1L,
                "1.0.0",
                "Domain Layer Convention",
                true,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
