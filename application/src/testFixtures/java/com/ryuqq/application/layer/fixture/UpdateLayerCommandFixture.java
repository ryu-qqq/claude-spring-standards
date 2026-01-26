package com.ryuqq.application.layer.fixture;

import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;

/**
 * UpdateLayerCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateLayerCommandFixture {

    private UpdateLayerCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 UpdateLayerCommand Fixture */
    public static UpdateLayerCommand defaultCommand() {
        return new UpdateLayerCommand(1L, "DOMAIN", "도메인 레이어", "업데이트된 설명", 1);
    }

    /** 특정 ID로 업데이트하는 Command */
    public static UpdateLayerCommand commandWithId(Long id) {
        return new UpdateLayerCommand(id, "DOMAIN", "도메인 레이어", "업데이트된 설명", 1);
    }

    /** 다른 코드로 업데이트하는 Command */
    public static UpdateLayerCommand commandWithCode(String code) {
        return new UpdateLayerCommand(1L, code, code + " 레이어", "업데이트된 설명", 1);
    }

    /** 설명 없이 업데이트하는 Command */
    public static UpdateLayerCommand commandWithoutDescription(Long id) {
        return new UpdateLayerCommand(id, "DOMAIN", "도메인 레이어", null, 1);
    }

    /** 커스텀 Command 생성 */
    public static UpdateLayerCommand customCommand(
            Long id, String code, String name, String description, int orderIndex) {
        return new UpdateLayerCommand(id, code, name, description, orderIndex);
    }
}
