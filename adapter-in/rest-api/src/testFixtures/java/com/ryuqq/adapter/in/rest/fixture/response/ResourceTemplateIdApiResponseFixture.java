package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.resourcetemplate.dto.response.ResourceTemplateIdApiResponse;

/**
 * ResourceTemplateIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ResourceTemplateIdApiResponseFixture {

    private ResourceTemplateIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ResourceTemplateIdApiResponse valid() {
        return ResourceTemplateIdApiResponse.of(1L);
    }
}
