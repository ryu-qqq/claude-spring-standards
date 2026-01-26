package com.ryuqq.application.architecture.fixture;

import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import java.util.List;

/**
 * CreateArchitectureCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateArchitectureCommandFixture {

    private CreateArchitectureCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 CreateArchitectureCommand Fixture */
    public static CreateArchitectureCommand defaultCommand() {
        return new CreateArchitectureCommand(
                1L, "hexagonal-architecture", "HEXAGONAL", null, null, List.of());
    }

    /** 설명이 포함된 Command */
    public static CreateArchitectureCommand commandWithDescription() {
        return new CreateArchitectureCommand(
                1L,
                "hexagonal-architecture",
                "HEXAGONAL",
                "Ports and Adapters 패턴을 사용한 헥사고날 아키텍처",
                null,
                List.of());
    }

    /** 원칙이 포함된 Command */
    public static CreateArchitectureCommand commandWithPrinciples() {
        return new CreateArchitectureCommand(
                1L,
                "clean-architecture",
                "CLEAN",
                null,
                List.of("DIP", "SRP", "OCP", "ISP"),
                List.of());
    }

    /** 설명과 원칙이 모두 포함된 Command */
    public static CreateArchitectureCommand commandWithDescriptionAndPrinciples() {
        return new CreateArchitectureCommand(
                1L,
                "layered-architecture",
                "LAYERED",
                "계층형 아키텍처 패턴",
                List.of("SRP", "SOC"),
                List.of());
    }

    /** 커스텀 Command 생성 */
    public static CreateArchitectureCommand customCommand(
            Long techStackId,
            String name,
            String patternType,
            String patternDescription,
            List<String> patternPrinciples,
            List<String> referenceLinks) {
        return new CreateArchitectureCommand(
                techStackId,
                name,
                patternType,
                patternDescription,
                patternPrinciples,
                referenceLinks);
    }
}
