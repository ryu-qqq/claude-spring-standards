package com.ryuqq.application.techstack.fixture;

import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import java.util.List;

/**
 * UpdateTechStackCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateTechStackCommandFixture {

    private UpdateTechStackCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 UpdateTechStackCommand Fixture */
    public static UpdateTechStackCommand defaultCommand() {
        return new UpdateTechStackCommand(
                1L,
                "Spring Boot 3.6",
                "ACTIVE",
                "JAVA",
                "22",
                List.of(),
                "SPRING_BOOT",
                "3.6.0",
                List.of(),
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    /** 비권장 상태로 변경하는 Command */
    public static UpdateTechStackCommand deprecateCommand(Long id) {
        return new UpdateTechStackCommand(
                id,
                "Deprecated TechStack",
                "DEPRECATED",
                "JAVA",
                "17",
                List.of(),
                "SPRING_BOOT",
                "2.7.18",
                List.of(),
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    /** 보관 상태로 변경하는 Command */
    public static UpdateTechStackCommand archiveCommand(Long id) {
        return new UpdateTechStackCommand(
                id,
                "Archived TechStack",
                "ARCHIVED",
                "JAVA",
                "11",
                List.of(),
                "SPRING_BOOT",
                "2.5.15",
                List.of(),
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    /** 커스텀 Command 생성 */
    public static UpdateTechStackCommand customCommand(
            Long id,
            String name,
            String status,
            String languageType,
            String languageVersion,
            List<String> languageFeatures,
            String frameworkType,
            String frameworkVersion,
            List<String> frameworkModules,
            String platformType,
            String runtimeEnvironment,
            String buildToolType,
            String buildConfigFile,
            List<String> referenceLinks) {
        return new UpdateTechStackCommand(
                id,
                name,
                status,
                languageType,
                languageVersion,
                languageFeatures,
                frameworkType,
                frameworkVersion,
                frameworkModules,
                platformType,
                runtimeEnvironment,
                buildToolType,
                buildConfigFile,
                referenceLinks);
    }
}
