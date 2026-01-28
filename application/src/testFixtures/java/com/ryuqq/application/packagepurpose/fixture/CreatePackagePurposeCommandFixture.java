package com.ryuqq.application.packagepurpose.fixture;

import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;

/**
 * CreatePackagePurposeCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreatePackagePurposeCommandFixture {

    private CreatePackagePurposeCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 CreatePackagePurposeCommand Fixture */
    public static CreatePackagePurposeCommand defaultCommand() {
        return new CreatePackagePurposeCommand(1L, "AGGREGATE", "Aggregate", "Aggregate 패키지 목적");
    }

    /** APPLICATION 레이어 타입 Command */
    public static CreatePackagePurposeCommand applicationLayerCommand() {
        return new CreatePackagePurposeCommand(2L, "SERVICE", "Service", "Service 패키지 목적");
    }

    /** 커스텀 Command 생성 */
    public static CreatePackagePurposeCommand customCommand(
            Long structureId, String code, String name, String description) {
        return new CreatePackagePurposeCommand(structureId, code, name, description);
    }
}
