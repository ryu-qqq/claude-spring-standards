package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.convention.dto.request.UpdateConventionApiRequest;

/**
 * UpdateConventionApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateConventionApiRequestFixture {

    /** 기본 Module ID */
    private static final Long DEFAULT_MODULE_ID = 1L;

    private UpdateConventionApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateConventionApiRequest valid() {
        return new UpdateConventionApiRequest(
                DEFAULT_MODULE_ID, "1.0.0", "Module Convention", true);
    }

    public static UpdateConventionApiRequest invalidWithNullModuleId() {
        return new UpdateConventionApiRequest(null, "1.0.0", "Description", true);
    }

    public static UpdateConventionApiRequest invalidWithBlankVersion() {
        return new UpdateConventionApiRequest(DEFAULT_MODULE_ID, "", "Description", true);
    }

    public static UpdateConventionApiRequest invalidWithNullActive() {
        return new UpdateConventionApiRequest(DEFAULT_MODULE_ID, "1.0.0", "Description", null);
    }

    public static UpdateConventionApiRequest withModuleId(Long moduleId) {
        return new UpdateConventionApiRequest(
                moduleId, "1.0.0", "Convention for Module " + moduleId, true);
    }
}
