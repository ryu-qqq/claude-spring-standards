package com.ryuqq.application.architecture.fixture;

import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import java.util.List;

/**
 * UpdateArchitectureCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateArchitectureCommandFixture {

    private UpdateArchitectureCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 UpdateArchitectureCommand Fixture */
    public static UpdateArchitectureCommand defaultCommand() {
        return new UpdateArchitectureCommand(
                1L, "updated-architecture", "CLEAN", "업데이트된 설명", List.of("DIP", "SRP"), List.of());
    }

    /** 빈 설명과 원칙으로 업데이트하는 Command */
    public static UpdateArchitectureCommand commandWithEmptyDescriptionAndPrinciples(Long id) {
        return new UpdateArchitectureCommand(
                id, "updated-architecture", "HEXAGONAL", null, null, List.of());
    }

    /** 커스텀 Command 생성 */
    public static UpdateArchitectureCommand customCommand(
            Long id,
            String name,
            String patternType,
            String patternDescription,
            List<String> patternPrinciples,
            List<String> referenceLinks) {
        return new UpdateArchitectureCommand(
                id, name, patternType, patternDescription, patternPrinciples, referenceLinks);
    }
}
