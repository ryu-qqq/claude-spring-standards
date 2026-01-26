package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.module.dto.request.SearchModulesCursorApiRequest;
import java.util.List;

/**
 * SearchModulesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchModulesCursorApiRequestFixture {

    private SearchModulesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchModulesCursorApiRequest valid() {
        return new SearchModulesCursorApiRequest(null, 20, null);
    }

    public static SearchModulesCursorApiRequest validWithCursor() {
        return new SearchModulesCursorApiRequest("100", 20, null);
    }

    public static SearchModulesCursorApiRequest validWithLayerIds() {
        return new SearchModulesCursorApiRequest(null, 20, List.of(1L, 2L));
    }

    public static SearchModulesCursorApiRequest validWithLayerIdsAndCursor() {
        return new SearchModulesCursorApiRequest("100", 20, List.of(1L, 2L));
    }

    public static SearchModulesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchModulesCursorApiRequest(null, 101, null);
    }
}
