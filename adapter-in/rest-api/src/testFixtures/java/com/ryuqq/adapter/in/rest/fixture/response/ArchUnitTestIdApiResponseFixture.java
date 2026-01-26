package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.archunittest.dto.response.ArchUnitTestIdApiResponse;

/**
 * ArchUnitTestIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchUnitTestIdApiResponseFixture {

    private ArchUnitTestIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ArchUnitTestIdApiResponse valid() {
        return ArchUnitTestIdApiResponse.of(1L);
    }
}
