package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.techstack.dto.response.TechStackIdApiResponse;

/**
 * TechStackIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TechStackIdApiResponseFixture {

    private TechStackIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static TechStackIdApiResponse valid() {
        return TechStackIdApiResponse.of(1L);
    }
}
