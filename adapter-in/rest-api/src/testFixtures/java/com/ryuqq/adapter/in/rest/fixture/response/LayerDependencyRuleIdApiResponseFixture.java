package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleIdApiResponse;

/**
 * LayerDependencyRuleIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class LayerDependencyRuleIdApiResponseFixture {

    private LayerDependencyRuleIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static LayerDependencyRuleIdApiResponse valid() {
        return LayerDependencyRuleIdApiResponse.of(1L);
    }
}
