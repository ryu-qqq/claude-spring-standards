package com.ryuqq.application.codingrule.fixture;

import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import java.util.List;

/**
 * CreateCodingRuleCommand Test Fixture
 *
 * @author development-team
 */
public final class CreateCodingRuleCommandFixture {

    private CreateCodingRuleCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateCodingRuleCommand defaultCommand() {
        return new CreateCodingRuleCommand(
                1L,
                null,
                "DOM-001",
                "Lombok 사용 금지",
                "BLOCKER",
                "ANNOTATION",
                "도메인 레이어에서 Lombok 사용 금지",
                "Pure Java 원칙",
                false,
                List.of("CLASS"),
                null,
                null,
                null);
    }
}
