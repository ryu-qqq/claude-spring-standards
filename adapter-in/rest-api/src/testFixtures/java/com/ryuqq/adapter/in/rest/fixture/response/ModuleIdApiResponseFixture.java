package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.module.dto.response.ModuleIdApiResponse;

/**
 * ModuleIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ModuleIdApiResponseFixture {

    private ModuleIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ModuleIdApiResponse valid() {
        return ModuleIdApiResponse.of(1L);
    }
}
