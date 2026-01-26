package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.packagestructure.dto.request.CreatePackageStructureApiRequest;
import java.util.List;

/**
 * CreatePackageStructureApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreatePackageStructureApiRequestFixture {

    private CreatePackageStructureApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreatePackageStructureApiRequest valid() {
        return new CreatePackageStructureApiRequest(
                1L,
                "{base}.domain.{bc}.aggregate",
                List.of("CLASS", "RECORD"),
                ".*Aggregate",
                "Aggregate",
                "Aggregate Root 패키지");
    }

    public static CreatePackageStructureApiRequest validMinimal() {
        return new CreatePackageStructureApiRequest(1L, "{base}.domain", null, null, null, null);
    }

    public static CreatePackageStructureApiRequest invalidWithNullModuleId() {
        return new CreatePackageStructureApiRequest(null, "path", null, null, null, null);
    }

    public static CreatePackageStructureApiRequest invalidWithBlankPathPattern() {
        return new CreatePackageStructureApiRequest(1L, "", null, null, null, null);
    }

    public static CreatePackageStructureApiRequest invalidWithLongPathPattern() {
        return new CreatePackageStructureApiRequest(1L, "A".repeat(301), null, null, null, null);
    }
}
