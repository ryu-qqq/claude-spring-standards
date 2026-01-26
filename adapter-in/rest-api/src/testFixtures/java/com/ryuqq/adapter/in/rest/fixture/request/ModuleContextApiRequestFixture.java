package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.mcp.dto.request.ModuleContextApiRequest;

/**
 * ModuleContextApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ModuleContextApiRequestFixture {

    private ModuleContextApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ModuleContextApiRequest valid() {
        return new ModuleContextApiRequest(1L);
    }

    public static ModuleContextApiRequest validWithoutClassTypeId() {
        return new ModuleContextApiRequest(null);
    }

    public static ModuleContextApiRequest withClassTypeId(Long classTypeId) {
        return new ModuleContextApiRequest(classTypeId);
    }
}
