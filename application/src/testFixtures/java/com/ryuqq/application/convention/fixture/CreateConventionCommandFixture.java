package com.ryuqq.application.convention.fixture;

import com.ryuqq.application.convention.dto.command.CreateConventionCommand;

/**
 * CreateConventionCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateConventionCommandFixture {

    private CreateConventionCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 CreateConventionCommand Fixture (DOMAIN Layer) */
    public static CreateConventionCommand defaultCommand() {
        return new CreateConventionCommand(1L, "1.0.0", "도메인 레이어 코딩 컨벤션");
    }

    /** APPLICATION 레이어 Command */
    public static CreateConventionCommand applicationLayerCommand() {
        return new CreateConventionCommand(2L, "1.0.0", "애플리케이션 레이어 코딩 컨벤션");
    }

    /** PERSISTENCE 레이어 Command */
    public static CreateConventionCommand persistenceLayerCommand() {
        return new CreateConventionCommand(3L, "1.0.0", "영속성 레이어 코딩 컨벤션");
    }

    /** REST_API 레이어 Command */
    public static CreateConventionCommand restApiLayerCommand() {
        return new CreateConventionCommand(4L, "1.0.0", "REST API 레이어 코딩 컨벤션");
    }

    /** TESTING 레이어 Command */
    public static CreateConventionCommand testingLayerCommand() {
        return new CreateConventionCommand(5L, "1.0.0", "테스트 레이어 코딩 컨벤션");
    }

    /** 특정 버전의 Command */
    public static CreateConventionCommand commandWithVersion(String version) {
        return new CreateConventionCommand(1L, version, "도메인 레이어 코딩 컨벤션");
    }

    /** 커스텀 Command 생성 */
    public static CreateConventionCommand customCommand(
            Long layerId, String version, String description) {
        return new CreateConventionCommand(layerId, version, description);
    }
}
