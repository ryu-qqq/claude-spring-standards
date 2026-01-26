package com.ryuqq.application.module.fixture;

import com.ryuqq.application.module.dto.command.CreateModuleCommand;

/**
 * CreateModuleCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateModuleCommandFixture {

    private CreateModuleCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 CreateModuleCommand Fixture */
    public static CreateModuleCommand defaultCommand() {
        return new CreateModuleCommand(1L, null, "domain", "도메인 모듈", "domain", ":domain");
    }

    /** 루트 모듈 Command */
    public static CreateModuleCommand rootModuleCommand() {
        return new CreateModuleCommand(
                1L, null, "root-module", "루트 모듈", "root-module", ":root-module");
    }

    /** 자식 모듈 Command */
    public static CreateModuleCommand childModuleCommand(Long parentModuleId) {
        return new CreateModuleCommand(
                2L,
                parentModuleId,
                "child-module",
                "자식 모듈",
                "application/child-module",
                ":application:child-module");
    }

    /** 커스텀 Command 생성 */
    public static CreateModuleCommand customCommand(
            Long layerId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier) {
        return new CreateModuleCommand(
                layerId, parentModuleId, name, description, modulePath, buildIdentifier);
    }
}
