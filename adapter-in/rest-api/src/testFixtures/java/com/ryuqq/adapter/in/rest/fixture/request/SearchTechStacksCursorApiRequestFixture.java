package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.techstack.dto.request.SearchTechStacksCursorApiRequest;

/**
 * SearchTechStacksCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchTechStacksCursorApiRequestFixture {

    private SearchTechStacksCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchTechStacksCursorApiRequest valid() {
        return new SearchTechStacksCursorApiRequest(null, 20, null, null);
    }

    public static SearchTechStacksCursorApiRequest validWithCursor() {
        return new SearchTechStacksCursorApiRequest("100", 20, null, null);
    }

    public static SearchTechStacksCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchTechStacksCursorApiRequest(null, 101, null, null);
    }

    public static SearchTechStacksCursorApiRequest validWithFilters() {
        return new SearchTechStacksCursorApiRequest(
                null, 20, "ACTIVE", java.util.List.of("BACKEND"));
    }
}
