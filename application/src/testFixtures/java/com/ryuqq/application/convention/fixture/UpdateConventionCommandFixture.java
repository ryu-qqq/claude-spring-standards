package com.ryuqq.application.convention.fixture;

import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;

/**
 * UpdateConventionCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateConventionCommandFixture {

    private UpdateConventionCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 UpdateConventionCommand Fixture (활성화) */
    public static UpdateConventionCommand defaultCommand() {
        return new UpdateConventionCommand(1L, 1L, "1.0.0", "업데이트된 도메인 레이어 코딩 컨벤션", true);
    }

    /** 비활성화 Command */
    public static UpdateConventionCommand deactivateCommand() {
        return new UpdateConventionCommand(1L, 1L, "1.0.0", "비활성화된 도메인 레이어 코딩 컨벤션", false);
    }

    /** 특정 ID로 업데이트하는 Command */
    public static UpdateConventionCommand commandWithId(Long id) {
        return new UpdateConventionCommand(id, 1L, "1.0.0", "업데이트된 도메인 레이어 코딩 컨벤션", true);
    }

    /** APPLICATION 레이어로 업데이트하는 Command */
    public static UpdateConventionCommand applicationLayerCommand() {
        return new UpdateConventionCommand(1L, 2L, "1.0.0", "업데이트된 애플리케이션 레이어 코딩 컨벤션", true);
    }

    /** 커스텀 Command 생성 */
    public static UpdateConventionCommand customCommand(
            Long id, Long layerId, String version, String description, boolean active) {
        return new UpdateConventionCommand(id, layerId, version, description, active);
    }
}
