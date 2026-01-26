package com.ryuqq.application.techstack.fixture;

import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import java.util.List;

/**
 * CreateTechStackCommand Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 Command DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateTechStackCommandFixture {

    private CreateTechStackCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 CreateTechStackCommand Fixture */
    public static CreateTechStackCommand defaultCommand() {
        return new CreateTechStackCommand(
                "Spring Boot 3.5",
                "JAVA",
                "21",
                List.of(),
                "SPRING_BOOT",
                "3.5.0",
                List.of(),
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    /** 언어 기능이 포함된 Command */
    public static CreateTechStackCommand commandWithLanguageFeatures() {
        return new CreateTechStackCommand(
                "Java 21 with Features",
                "JAVA",
                "21",
                List.of("VIRTUAL_THREAD", "RECORD", "PATTERN_MATCHING"),
                "SPRING_BOOT",
                "3.5.0",
                List.of(),
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    /** 프레임워크 모듈이 포함된 Command */
    public static CreateTechStackCommand commandWithFrameworkModules() {
        return new CreateTechStackCommand(
                "Spring Boot with Modules",
                "JAVA",
                "21",
                List.of(),
                "SPRING_BOOT",
                "3.5.0",
                List.of("WEB", "JPA", "SECURITY", "ACTUATOR"),
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    /** 커스텀 Command 생성 */
    public static CreateTechStackCommand customCommand(
            String name,
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
        return new CreateTechStackCommand(
                name,
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
