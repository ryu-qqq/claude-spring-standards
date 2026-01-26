package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.architecture.dto.request.SearchArchitecturesCursorApiRequest;
import java.util.Collections;
import java.util.List;

/**
 * SearchArchitecturesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchArchitecturesCursorApiRequestFixture {

    private SearchArchitecturesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchArchitecturesCursorApiRequest valid() {
        return new SearchArchitecturesCursorApiRequest(null, 20, Collections.emptyList());
    }

    public static SearchArchitecturesCursorApiRequest validWithCursor(String cursor) {
        return new SearchArchitecturesCursorApiRequest(cursor, 20, Collections.emptyList());
    }

    public static SearchArchitecturesCursorApiRequest validWithTechStackIds(
            List<Long> techStackIds) {
        return new SearchArchitecturesCursorApiRequest(null, 20, techStackIds);
    }

    public static SearchArchitecturesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchArchitecturesCursorApiRequest(null, 101, Collections.emptyList());
    }

    public static SearchArchitecturesCursorApiRequest invalidWithSizeTooSmall() {
        return new SearchArchitecturesCursorApiRequest(null, 0, Collections.emptyList());
    }
}
