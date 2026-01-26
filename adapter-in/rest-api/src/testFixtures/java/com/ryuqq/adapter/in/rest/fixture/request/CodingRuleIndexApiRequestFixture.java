package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.CodingRuleIndexApiRequest;
import java.util.List;

/**
 * CodingRuleIndexApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CodingRuleIndexApiRequestFixture {

    private CodingRuleIndexApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CodingRuleIndexApiRequest valid() {
        return new CodingRuleIndexApiRequest(null, null, null);
    }

    public static CodingRuleIndexApiRequest withConventionId(Long conventionId) {
        return new CodingRuleIndexApiRequest(conventionId, null, null);
    }

    public static CodingRuleIndexApiRequest withSeverities(List<String> severities) {
        return new CodingRuleIndexApiRequest(null, severities, null);
    }

    public static CodingRuleIndexApiRequest withCategories(List<String> categories) {
        return new CodingRuleIndexApiRequest(null, null, categories);
    }

    public static CodingRuleIndexApiRequest withFilters(
            Long conventionId, List<String> severities, List<String> categories) {
        return new CodingRuleIndexApiRequest(conventionId, severities, categories);
    }
}
