package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.packagestructure.dto.response.PackageStructureIdApiResponse;

/**
 * PackageStructureIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PackageStructureIdApiResponseFixture {

    private PackageStructureIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static PackageStructureIdApiResponse valid() {
        return PackageStructureIdApiResponse.of(1L);
    }
}
