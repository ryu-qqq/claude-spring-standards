package com.ryuqq.application.layerdependency.fixture;

import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;

/**
 * CreateLayerDependencyRuleCommand Test Fixture
 *
 * @author development-team
 */
public final class CreateLayerDependencyRuleCommandFixture {

    private CreateLayerDependencyRuleCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateLayerDependencyRuleCommand defaultCommand() {
        return new CreateLayerDependencyRuleCommand(1L, "DOMAIN", "APPLICATION", "ALLOWED", null);
    }
}
