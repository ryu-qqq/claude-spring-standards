package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.SearchCodingRulesCursorApiRequest;
import java.util.List;

/**
 * SearchCodingRulesCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchCodingRulesCursorApiRequestFixture {

    private SearchCodingRulesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchCodingRulesCursorApiRequest valid() {
        return new SearchCodingRulesCursorApiRequest(null, 20, null, null, null, null);
    }

    public static SearchCodingRulesCursorApiRequest validWithCursor() {
        return new SearchCodingRulesCursorApiRequest("100", 20, null, null, null, null);
    }

    public static SearchCodingRulesCursorApiRequest validWithCategories() {
        return new SearchCodingRulesCursorApiRequest(
                null, 20, List.of("ANNOTATION", "BEHAVIOR"), null, null, null);
    }

    public static SearchCodingRulesCursorApiRequest validWithSeverities() {
        return new SearchCodingRulesCursorApiRequest(
                null, 20, null, List.of("BLOCKER", "CRITICAL"), null, null);
    }

    public static SearchCodingRulesCursorApiRequest validWithSearch() {
        return new SearchCodingRulesCursorApiRequest(null, 20, null, null, "CODE", "CTR-001");
    }

    public static SearchCodingRulesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchCodingRulesCursorApiRequest(null, 101, null, null, null, null);
    }
}
