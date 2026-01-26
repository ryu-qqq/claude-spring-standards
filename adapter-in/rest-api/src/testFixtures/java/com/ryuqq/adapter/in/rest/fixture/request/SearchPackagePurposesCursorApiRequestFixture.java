package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.SearchPackagePurposesCursorApiRequest;
import java.util.List;

/**
 * SearchPackagePurposesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchPackagePurposesCursorApiRequestFixture {

    private SearchPackagePurposesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchPackagePurposesCursorApiRequest valid() {
        return new SearchPackagePurposesCursorApiRequest(null, 20, null, null, null);
    }

    public static SearchPackagePurposesCursorApiRequest validWithCursor() {
        return new SearchPackagePurposesCursorApiRequest("100", 20, null, null, null);
    }

    public static SearchPackagePurposesCursorApiRequest validWithStructureIds() {
        return new SearchPackagePurposesCursorApiRequest(null, 20, List.of(1L, 2L), null, null);
    }

    public static SearchPackagePurposesCursorApiRequest validWithSearch() {
        return new SearchPackagePurposesCursorApiRequest(null, 20, null, "CODE", "AGGREGATE");
    }

    public static SearchPackagePurposesCursorApiRequest validWithFilters() {
        return new SearchPackagePurposesCursorApiRequest(
                null, 20, List.of(1L), "NAME", "Aggregate");
    }

    public static SearchPackagePurposesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchPackagePurposesCursorApiRequest(null, 101, null, null, null);
    }
}
