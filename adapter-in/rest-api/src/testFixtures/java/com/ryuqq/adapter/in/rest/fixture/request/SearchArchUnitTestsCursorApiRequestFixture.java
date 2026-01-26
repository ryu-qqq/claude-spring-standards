package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.SearchArchUnitTestsCursorApiRequest;
import java.util.List;

/**
 * SearchArchUnitTestsCursorApiRequestFixture - SearchArchUnitTestsCursorApiRequest Test Fixture
 *
 * @author ryu-qqq
 */
public final class SearchArchUnitTestsCursorApiRequestFixture {

    private SearchArchUnitTestsCursorApiRequestFixture() {}

    public static SearchArchUnitTestsCursorApiRequest valid() {
        return new SearchArchUnitTestsCursorApiRequest(
                null, 20, List.of(1L, 2L), "CODE", "AGG-001", List.of("BLOCKER", "CRITICAL"));
    }

    public static SearchArchUnitTestsCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchArchUnitTestsCursorApiRequest(null, 101, null, null, null, null);
    }
}
