package com.ryuqq.application.layerdependency.fixture;

import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;

/**
 * UpdateLayerDependencyRuleCommand Test Fixture
 *
 * @author development-team
 */
public final class UpdateLayerDependencyRuleCommandFixture {

    private UpdateLayerDependencyRuleCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateLayerDependencyRuleCommand defaultCommand() {
        return new UpdateLayerDependencyRuleCommand(
                1L, 1L, "DOMAIN", "APPLICATION", "FORBIDDEN", null);
    }
}
