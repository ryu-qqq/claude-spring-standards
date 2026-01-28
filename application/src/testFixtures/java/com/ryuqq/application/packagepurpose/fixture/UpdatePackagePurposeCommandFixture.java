package com.ryuqq.application.packagepurpose.fixture;

import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;

/**
 * UpdatePackagePurposeCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdatePackagePurposeCommandFixture {

    private UpdatePackagePurposeCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 UpdatePackagePurposeCommand Fixture */
    public static UpdatePackagePurposeCommand defaultCommand() {
        return new UpdatePackagePurposeCommand(
                1L, "AGGREGATE", "Updated Aggregate", "업데이트된 Aggregate 패키지 목적");
    }

    /** 특정 ID로 업데이트하는 Command */
    public static UpdatePackagePurposeCommand commandWithId(Long id) {
        return new UpdatePackagePurposeCommand(
                id, "VALUE_OBJECT", "Value Object", "Value Object 패키지 목적");
    }

    /** 커스텀 Command 생성 */
    public static UpdatePackagePurposeCommand customCommand(
            Long packagePurposeId, String code, String name, String description) {
        return new UpdatePackagePurposeCommand(packagePurposeId, code, name, description);
    }
}
