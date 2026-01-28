package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.packagestructure.dto.response.PackageStructureApiResponse;

/**
 * PackageStructureApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PackageStructureApiResponseFixture {

    private PackageStructureApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static PackageStructureApiResponse valid() {
        return new PackageStructureApiResponse(
                1L,
                1L,
                "{base}.domain.{bc}.aggregate",
                "Aggregate Root 패키지",
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
