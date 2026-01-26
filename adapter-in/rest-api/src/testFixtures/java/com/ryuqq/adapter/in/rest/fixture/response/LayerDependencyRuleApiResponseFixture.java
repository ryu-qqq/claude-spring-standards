package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleApiResponse;

/**
 * LayerDependencyRuleApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class LayerDependencyRuleApiResponseFixture {

    private LayerDependencyRuleApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static LayerDependencyRuleApiResponse valid() {
        return new LayerDependencyRuleApiResponse(
                1L,
                1L,
                "DOMAIN",
                "APPLICATION",
                "ALLOWED",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    public static LayerDependencyRuleApiResponse conditional() {
        return new LayerDependencyRuleApiResponse(
                2L,
                1L,
                "DOMAIN",
                "APPLICATION",
                "CONDITIONAL",
                "특정 조건에서만 허용",
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
