package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.UpdateCodingRuleApiRequest;
import java.util.List;

/**
 * UpdateCodingRuleApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateCodingRuleApiRequestFixture {

    private UpdateCodingRuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateCodingRuleApiRequest valid() {
        return new UpdateCodingRuleApiRequest(
                null,
                "AGG-001",
                "Lombok 사용 금지",
                "BLOCKER",
                "ANNOTATION",
                "Domain Layer에서 Lombok 사용을 금지합니다",
                "Pure Java 원칙",
                false,
                List.of("AGGREGATE", "VALUE_OBJECT"),
                null);
    }

    public static UpdateCodingRuleApiRequest invalidWithBlankCode() {
        return new UpdateCodingRuleApiRequest(
                null,
                "",
                "Name",
                "BLOCKER",
                "ANNOTATION",
                "Description",
                null,
                false,
                List.of(),
                null);
    }
}
