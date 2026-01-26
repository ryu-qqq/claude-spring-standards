package com.ryuqq.application.module.fixture;

import com.ryuqq.application.module.dto.command.UpdateModuleCommand;

/**
 * UpdateModuleCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateModuleCommandFixture {

    private UpdateModuleCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 UpdateModuleCommand Fixture */
    public static UpdateModuleCommand defaultCommand() {
        return new UpdateModuleCommand(
                1L, null, "updated-domain", "업데이트된 도메인 모듈", "domain", ":domain");
    }

    /** 커스텀 Command 생성 */
    public static UpdateModuleCommand customCommand(
            Long moduleId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier) {
        return new UpdateModuleCommand(
                moduleId, parentModuleId, name, description, modulePath, buildIdentifier);
    }
}
