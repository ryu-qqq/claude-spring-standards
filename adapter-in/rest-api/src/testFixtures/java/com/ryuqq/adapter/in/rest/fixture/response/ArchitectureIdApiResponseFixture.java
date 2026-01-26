package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureIdApiResponse;

/**
 * ArchitectureIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchitectureIdApiResponseFixture {

    private ArchitectureIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ArchitectureIdApiResponse valid() {
        return ArchitectureIdApiResponse.of(1L);
    }
}
