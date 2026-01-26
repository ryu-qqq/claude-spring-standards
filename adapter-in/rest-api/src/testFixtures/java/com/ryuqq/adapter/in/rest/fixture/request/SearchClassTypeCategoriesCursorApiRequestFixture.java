package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.SearchClassTypeCategoriesCursorApiRequest;
import java.util.List;

/**
 * SearchClassTypeCategoriesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchClassTypeCategoriesCursorApiRequestFixture {

    private static final String DEFAULT_CURSOR = null;
    private static final Integer DEFAULT_SIZE = 20;
    private static final List<Long> DEFAULT_ARCHITECTURE_IDS = null;
    private static final String DEFAULT_SEARCH_FIELD = null;
    private static final String DEFAULT_SEARCH_WORD = null;

    private SearchClassTypeCategoriesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchClassTypeCategoriesCursorApiRequest valid() {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR,
                DEFAULT_SIZE,
                DEFAULT_ARCHITECTURE_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypeCategoriesCursorApiRequest withCursor(String cursor) {
        return new SearchClassTypeCategoriesCursorApiRequest(
                cursor,
                DEFAULT_SIZE,
                DEFAULT_ARCHITECTURE_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypeCategoriesCursorApiRequest withSize(Integer size) {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR,
                size,
                DEFAULT_ARCHITECTURE_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypeCategoriesCursorApiRequest withArchitectureIds(
            List<Long> architectureIds) {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR,
                DEFAULT_SIZE,
                architectureIds,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypeCategoriesCursorApiRequest withSearch(
            String searchField, String searchWord) {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR, DEFAULT_SIZE, DEFAULT_ARCHITECTURE_IDS, searchField, searchWord);
    }

    public static SearchClassTypeCategoriesCursorApiRequest invalidWithSizeTooSmall() {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR,
                0,
                DEFAULT_ARCHITECTURE_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypeCategoriesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR,
                101,
                DEFAULT_ARCHITECTURE_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypeCategoriesCursorApiRequest invalidWithSearchWordTooLong() {
        return new SearchClassTypeCategoriesCursorApiRequest(
                DEFAULT_CURSOR, DEFAULT_SIZE, DEFAULT_ARCHITECTURE_IDS, "CODE", "A".repeat(256));
    }
}
