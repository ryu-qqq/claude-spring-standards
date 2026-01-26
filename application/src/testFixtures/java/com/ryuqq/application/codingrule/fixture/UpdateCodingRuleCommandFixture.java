package com.ryuqq.application.codingrule.fixture;

import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import java.util.List;

/**
 * UpdateCodingRuleCommand Test Fixture
 *
 * @author development-team
 */
public final class UpdateCodingRuleCommandFixture {

    private UpdateCodingRuleCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateCodingRuleCommand defaultCommand() {
        return new UpdateCodingRuleCommand(
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
