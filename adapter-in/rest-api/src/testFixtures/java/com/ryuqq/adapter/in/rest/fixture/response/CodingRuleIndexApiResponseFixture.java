package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIndexApiResponse;
import java.util.List;

/**
 * CodingRuleIndexApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CodingRuleIndexApiResponseFixture {

    private CodingRuleIndexApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CodingRuleIndexApiResponse valid() {
        return CodingRuleIndexApiResponse.of("AGG-001", "Lombok 사용 금지", "BLOCKER", "ANNOTATION");
    }

    public static CodingRuleIndexApiResponse withCode(String code) {
        return CodingRuleIndexApiResponse.of(code, "Rule " + code, "BLOCKER", "STRUCTURE");
    }

    public static List<CodingRuleIndexApiResponse> validList() {
        return List.of(
                CodingRuleIndexApiResponse.of("AGG-001", "Lombok 사용 금지", "BLOCKER", "ANNOTATION"),
                CodingRuleIndexApiResponse.of("AGG-002", "Getter 체이닝 금지", "BLOCKER", "BEHAVIOR"),
                CodingRuleIndexApiResponse.of(
                        "CTR-001", "Controller @Transactional 금지", "CRITICAL", "ANNOTATION"));
    }
}
