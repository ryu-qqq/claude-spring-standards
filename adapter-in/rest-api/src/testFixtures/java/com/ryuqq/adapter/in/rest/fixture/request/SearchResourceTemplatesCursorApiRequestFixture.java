package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.SearchResourceTemplatesCursorApiRequest;
import java.util.List;

/**
 * SearchResourceTemplatesCursorApiRequestFixture Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchResourceTemplatesCursorApiRequestFixture {

    private SearchResourceTemplatesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchResourceTemplatesCursorApiRequest valid() {
        return new SearchResourceTemplatesCursorApiRequest(
                null, 20, List.of(1L), List.of("CONFIG"), List.of("YAML"));
    }

    public static SearchResourceTemplatesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchResourceTemplatesCursorApiRequest(null, 101, null, null, null);
    }
}
