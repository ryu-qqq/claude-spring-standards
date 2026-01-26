package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.convention.dto.request.CreateConventionApiRequest;

/**
 * CreateConventionApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateConventionApiRequestFixture {

    /** 기본 Module ID */
    private static final Long DEFAULT_MODULE_ID = 1L;

    private CreateConventionApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateConventionApiRequest valid() {
        return new CreateConventionApiRequest(DEFAULT_MODULE_ID, "1.0.0", "Module Convention");
    }

    public static CreateConventionApiRequest invalidWithNullModuleId() {
        return new CreateConventionApiRequest(null, "1.0.0", "Description");
    }

    public static CreateConventionApiRequest invalidWithBlankVersion() {
        return new CreateConventionApiRequest(DEFAULT_MODULE_ID, "", "Description");
    }

    public static CreateConventionApiRequest invalidWithLongVersion() {
        return new CreateConventionApiRequest(DEFAULT_MODULE_ID, "A".repeat(21), "Description");
    }

    public static CreateConventionApiRequest invalidWithBlankDescription() {
        return new CreateConventionApiRequest(DEFAULT_MODULE_ID, "1.0.0", "");
    }

    public static CreateConventionApiRequest invalidWithLongDescription() {
        return new CreateConventionApiRequest(DEFAULT_MODULE_ID, "1.0.0", "A".repeat(1001));
    }

    public static CreateConventionApiRequest withModuleId(Long moduleId) {
        return new CreateConventionApiRequest(
                moduleId, "1.0.0", "Convention for Module " + moduleId);
    }
}
