package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.layer.dto.request.SearchLayersCursorApiRequest;
import java.util.Collections;
import java.util.List;

/**
 * SearchLayersCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchLayersCursorApiRequestFixture {

    private static final Integer DEFAULT_SIZE = 20;

    private SearchLayersCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchLayersCursorApiRequest valid() {
        return new SearchLayersCursorApiRequest(
                null, DEFAULT_SIZE, Collections.emptyList(), null, null);
    }

    public static SearchLayersCursorApiRequest validWithCursor(String cursor) {
        return new SearchLayersCursorApiRequest(
                cursor, DEFAULT_SIZE, Collections.emptyList(), null, null);
    }

    public static SearchLayersCursorApiRequest validWithArchitectureIds(
            List<Long> architectureIds) {
        return new SearchLayersCursorApiRequest(null, DEFAULT_SIZE, architectureIds, null, null);
    }

    public static SearchLayersCursorApiRequest validWithSearch(
            String searchField, String searchWord) {
        return new SearchLayersCursorApiRequest(
                null, DEFAULT_SIZE, Collections.emptyList(), searchField, searchWord);
    }

    public static SearchLayersCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchLayersCursorApiRequest(null, 101, Collections.emptyList(), null, null);
    }

    public static SearchLayersCursorApiRequest invalidWithSizeTooSmall() {
        return new SearchLayersCursorApiRequest(null, 0, Collections.emptyList(), null, null);
    }
}
