package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIdApiResponse;

/**
 * CodingRuleIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CodingRuleIdApiResponseFixture {

    private CodingRuleIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CodingRuleIdApiResponse valid() {
        return CodingRuleIdApiResponse.of(1L);
    }
}
