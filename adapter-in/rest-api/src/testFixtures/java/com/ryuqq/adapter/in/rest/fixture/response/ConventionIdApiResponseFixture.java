package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.convention.dto.response.ConventionIdApiResponse;

/**
 * ConventionIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ConventionIdApiResponseFixture {

    private ConventionIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ConventionIdApiResponse valid() {
        return ConventionIdApiResponse.of(1L);
    }
}
