package com.ryuqq.application.archunittest.fixture;

import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;

/**
 * UpdateArchUnitTestCommand Test Fixture
 *
 * @author development-team
 */
public final class UpdateArchUnitTestCommandFixture {

    private UpdateArchUnitTestCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateArchUnitTestCommand defaultCommand() {
        return new UpdateArchUnitTestCommand(
                1L,
                "ARCH-001",
                "업데이트된 Lombok 금지 검증",
                "업데이트된 설명",
                "DomainArchTest",
                "updated_lombok_should_not_be_used",
                "@ArchTest\nstatic final ArchRule updated_lombok_should_not_be_used = ...",
                "CRITICAL");
    }
}
