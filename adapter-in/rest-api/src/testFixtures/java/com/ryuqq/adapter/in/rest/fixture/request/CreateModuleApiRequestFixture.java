package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.module.dto.request.CreateModuleApiRequest;

/**
 * CreateModuleApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateModuleApiRequestFixture {

    private CreateModuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateModuleApiRequest valid() {
        return new CreateModuleApiRequest(
                1L,
                null,
                "adapter-in-rest-api",
                "REST API Adapter",
                "adapter-in/rest-api",
                ":adapter-in:rest-api");
    }

    public static CreateModuleApiRequest invalidWithNullLayerId() {
        return new CreateModuleApiRequest(null, null, "module", null, "module", ":module");
    }

    public static CreateModuleApiRequest invalidWithBlankName() {
        return new CreateModuleApiRequest(1L, null, "", null, "module", ":module");
    }

    public static CreateModuleApiRequest invalidWithLongName() {
        return new CreateModuleApiRequest(1L, null, "A".repeat(101), null, "module", ":module");
    }
}
