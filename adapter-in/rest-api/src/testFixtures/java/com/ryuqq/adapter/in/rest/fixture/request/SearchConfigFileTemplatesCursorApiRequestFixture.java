package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.SearchConfigFileTemplatesCursorApiRequest;
import java.util.List;

/**
 * SearchConfigFileTemplatesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchConfigFileTemplatesCursorApiRequestFixture {

    private SearchConfigFileTemplatesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchConfigFileTemplatesCursorApiRequest valid() {
        return new SearchConfigFileTemplatesCursorApiRequest(
                null, 20, null, null, null, null, null);
    }

    public static SearchConfigFileTemplatesCursorApiRequest validWithCursor() {
        return new SearchConfigFileTemplatesCursorApiRequest(
                "100", 20, null, null, null, null, null);
    }

    public static SearchConfigFileTemplatesCursorApiRequest validWithFilters() {
        return new SearchConfigFileTemplatesCursorApiRequest(
                null,
                20,
                List.of("CLAUDE", "CURSOR"),
                List.of(1L, 2L),
                List.of(1L),
                List.of("MAIN_CONFIG", "SKILL"),
                true);
    }

    public static SearchConfigFileTemplatesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchConfigFileTemplatesCursorApiRequest(
                null, 101, null, null, null, null, null);
    }
}
