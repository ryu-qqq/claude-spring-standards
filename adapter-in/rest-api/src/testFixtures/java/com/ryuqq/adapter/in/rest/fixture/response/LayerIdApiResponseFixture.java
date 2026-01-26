package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.layer.dto.response.LayerIdApiResponse;

/**
 * LayerIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class LayerIdApiResponseFixture {

    private LayerIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static LayerIdApiResponse valid() {
        return LayerIdApiResponse.of(1L);
    }

    public static LayerIdApiResponse withId(Long id) {
        return LayerIdApiResponse.of(id);
    }
}
