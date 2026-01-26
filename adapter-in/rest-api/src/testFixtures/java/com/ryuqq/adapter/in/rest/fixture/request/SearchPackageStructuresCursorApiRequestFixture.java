package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.packagestructure.dto.request.SearchPackageStructuresCursorApiRequest;
import java.util.List;

/**
 * SearchPackageStructuresCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchPackageStructuresCursorApiRequestFixture {

    private SearchPackageStructuresCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchPackageStructuresCursorApiRequest valid() {
        return new SearchPackageStructuresCursorApiRequest(null, 20, null);
    }

    public static SearchPackageStructuresCursorApiRequest validWithCursor() {
        return new SearchPackageStructuresCursorApiRequest("100", 20, null);
    }

    public static SearchPackageStructuresCursorApiRequest validWithModuleIds() {
        return new SearchPackageStructuresCursorApiRequest(null, 20, List.of(1L, 2L));
    }

    public static SearchPackageStructuresCursorApiRequest validWithFilters() {
        return new SearchPackageStructuresCursorApiRequest(null, 20, List.of(1L));
    }

    public static SearchPackageStructuresCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchPackageStructuresCursorApiRequest(null, 101, null);
    }
}
