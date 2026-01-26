package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.CreateCodingRuleApiRequest;
import java.util.List;

/**
 * CreateCodingRuleApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateCodingRuleApiRequestFixture {

    private CreateCodingRuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateCodingRuleApiRequest valid() {
        return new CreateCodingRuleApiRequest(
                1L,
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

    public static CreateCodingRuleApiRequest invalidWithNullConventionId() {
        return new CreateCodingRuleApiRequest(
                null,
                null,
                "AGG-001",
                "Name",
                "BLOCKER",
                "ANNOTATION",
                "Description",
                null,
                false,
                List.of(),
                null);
    }

    public static CreateCodingRuleApiRequest invalidWithBlankCode() {
        return new CreateCodingRuleApiRequest(
                1L,
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

    public static CreateCodingRuleApiRequest invalidWithLongCode() {
        return new CreateCodingRuleApiRequest(
                1L,
                null,
                "A".repeat(51),
                "Name",
                "BLOCKER",
                "ANNOTATION",
                "Description",
                null,
                false,
                List.of(),
                null);
    }

    public static CreateCodingRuleApiRequest invalidWithNullAutoFixable() {
        return new CreateCodingRuleApiRequest(
                1L,
                null,
                "AGG-001",
                "Name",
                "BLOCKER",
                "ANNOTATION",
                "Description",
                null,
                null,
                List.of(),
                null);
    }
}
