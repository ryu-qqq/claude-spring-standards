package com.ryuqq.application.archunittest.fixture;

import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;

/**
 * CreateArchUnitTestCommand Test Fixture
 *
 * @author development-team
 */
public final class CreateArchUnitTestCommandFixture {

    private CreateArchUnitTestCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateArchUnitTestCommand defaultCommand() {
        return new CreateArchUnitTestCommand(
                1L,
                "ARCH-001",
                "Lombok 금지 검증",
                "Domain 레이어에서 Lombok 사용 금지",
                "DomainArchTest",
                "lombok_should_not_be_used",
                "@ArchTest\nstatic final ArchRule lombok_should_not_be_used = ...",
                "BLOCKER");
    }
}
