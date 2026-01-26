package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.module.dto.request.UpdateModuleApiRequest;

/**
 * UpdateModuleApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateModuleApiRequestFixture {

    private UpdateModuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateModuleApiRequest valid() {
        return new UpdateModuleApiRequest(
                null,
                "adapter-in-rest-api",
                "REST API Adapter",
                "adapter-in/rest-api",
                ":adapter-in:rest-api");
    }

    public static UpdateModuleApiRequest invalidWithBlankName() {
        return new UpdateModuleApiRequest(null, "", null, "module", ":module");
    }
}
