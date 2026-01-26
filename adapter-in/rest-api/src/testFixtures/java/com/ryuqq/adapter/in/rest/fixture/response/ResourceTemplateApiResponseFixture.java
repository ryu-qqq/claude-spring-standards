package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.resourcetemplate.dto.response.ResourceTemplateApiResponse;

/**
 * ResourceTemplateApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ResourceTemplateApiResponseFixture {

    private ResourceTemplateApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ResourceTemplateApiResponse valid() {
        return new ResourceTemplateApiResponse(
                1L,
                1L,
                "DOMAIN",
                "src/main/java/Order.java",
                "JAVA",
                "Order Aggregate",
                "public class Order {}",
                true,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
