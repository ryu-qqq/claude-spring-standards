package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.module.dto.response.ModuleApiResponse;

/**
 * ModuleApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ModuleApiResponseFixture {

    private ModuleApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ModuleApiResponse valid() {
        return new ModuleApiResponse(
                1L,
                1L,
                null,
                "adapter-in-rest-api",
                "REST API Adapter",
                "adapter-in/rest-api",
                ":adapter-in:rest-api",
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
