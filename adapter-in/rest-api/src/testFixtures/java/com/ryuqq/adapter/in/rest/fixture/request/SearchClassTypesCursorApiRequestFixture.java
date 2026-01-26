package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtype.dto.request.SearchClassTypesCursorApiRequest;
import java.util.List;

/**
 * SearchClassTypesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchClassTypesCursorApiRequestFixture {

    private static final String DEFAULT_CURSOR = null;
    private static final Integer DEFAULT_SIZE = 20;
    private static final List<Long> DEFAULT_CATEGORY_IDS = null;
    private static final String DEFAULT_SEARCH_FIELD = null;
    private static final String DEFAULT_SEARCH_WORD = null;

    private SearchClassTypesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchClassTypesCursorApiRequest valid() {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR,
                DEFAULT_SIZE,
                DEFAULT_CATEGORY_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypesCursorApiRequest withCursor(String cursor) {
        return new SearchClassTypesCursorApiRequest(
                cursor,
                DEFAULT_SIZE,
                DEFAULT_CATEGORY_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypesCursorApiRequest withSize(Integer size) {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR,
                size,
                DEFAULT_CATEGORY_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypesCursorApiRequest withCategoryIds(List<Long> categoryIds) {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR,
                DEFAULT_SIZE,
                categoryIds,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypesCursorApiRequest withSearch(
            String searchField, String searchWord) {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR, DEFAULT_SIZE, DEFAULT_CATEGORY_IDS, searchField, searchWord);
    }

    public static SearchClassTypesCursorApiRequest invalidWithSizeTooSmall() {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR, 0, DEFAULT_CATEGORY_IDS, DEFAULT_SEARCH_FIELD, DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR,
                101,
                DEFAULT_CATEGORY_IDS,
                DEFAULT_SEARCH_FIELD,
                DEFAULT_SEARCH_WORD);
    }

    public static SearchClassTypesCursorApiRequest invalidWithSearchWordTooLong() {
        return new SearchClassTypesCursorApiRequest(
                DEFAULT_CURSOR, DEFAULT_SIZE, DEFAULT_CATEGORY_IDS, "CODE", "A".repeat(256));
    }
}
