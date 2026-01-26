package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.convention.dto.request.SearchConventionsCursorApiRequest;
import java.util.List;

/**
 * SearchConventionsCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchConventionsCursorApiRequestFixture {

    private SearchConventionsCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchConventionsCursorApiRequest valid() {
        return new SearchConventionsCursorApiRequest(null, 20, null);
    }

    public static SearchConventionsCursorApiRequest validWithCursor() {
        return new SearchConventionsCursorApiRequest("100", 20, null);
    }

    public static SearchConventionsCursorApiRequest validWithModuleIds() {
        return new SearchConventionsCursorApiRequest(null, 20, List.of(1L, 2L));
    }

    public static SearchConventionsCursorApiRequest validWithModuleIdsAndCursor() {
        return new SearchConventionsCursorApiRequest("100", 20, List.of(1L, 2L));
    }

    public static SearchConventionsCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchConventionsCursorApiRequest(null, 101, null);
    }
}
