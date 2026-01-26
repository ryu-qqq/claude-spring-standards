package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.packagestructure.dto.request.UpdatePackageStructureApiRequest;
import java.util.List;

/**
 * UpdatePackageStructureApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdatePackageStructureApiRequestFixture {

    private UpdatePackageStructureApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdatePackageStructureApiRequest valid() {
        return new UpdatePackageStructureApiRequest(
                "{base}.domain.{bc}.aggregate",
                List.of("CLASS", "RECORD"),
                ".*Aggregate",
                "Aggregate",
                "Aggregate Root 패키지");
    }

    public static UpdatePackageStructureApiRequest invalidWithBlankPathPattern() {
        return new UpdatePackageStructureApiRequest("", List.of(), "pattern", "suffix", "desc");
    }

    public static UpdatePackageStructureApiRequest invalidWithNullAllowedClassTypes() {
        return new UpdatePackageStructureApiRequest("path", null, "pattern", "suffix", "desc");
    }
}
