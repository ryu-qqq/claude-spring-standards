package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.SearchZeroToleranceRulesCursorApiRequest;
import java.util.List;

/**
 * SearchZeroToleranceRulesCursorApiRequestFixture - SearchZeroToleranceRulesCursorApiRequest Test
 * Fixture
 *
 * @author ryu-qqq
 */
public final class SearchZeroToleranceRulesCursorApiRequestFixture {

    private SearchZeroToleranceRulesCursorApiRequestFixture() {}

    public static SearchZeroToleranceRulesCursorApiRequest valid() {
        return new SearchZeroToleranceRulesCursorApiRequest(
                null,
                20,
                List.of(1L, 2L),
                List.of("REGEX", "AST"),
                "TYPE",
                "LOMBOK_IN_DOMAIN",
                true);
    }

    public static SearchZeroToleranceRulesCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchZeroToleranceRulesCursorApiRequest(
                null, 101, null, null, null, null, null);
    }
}
