package com.ryuqq.domain.techstack.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.BuildConfigFile;
import com.ryuqq.domain.techstack.vo.BuildToolType;
import com.ryuqq.domain.techstack.vo.FrameworkModules;
import com.ryuqq.domain.techstack.vo.FrameworkType;
import com.ryuqq.domain.techstack.vo.FrameworkVersion;
import com.ryuqq.domain.techstack.vo.LanguageFeatures;
import com.ryuqq.domain.techstack.vo.LanguageType;
import com.ryuqq.domain.techstack.vo.LanguageVersion;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.RuntimeEnvironment;
import com.ryuqq.domain.techstack.vo.TechStackName;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * TechStack Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 TechStack 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TechStackFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private TechStackFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 TechStack Fixture (신규 생성) */
    public static TechStack defaultNewTechStack() {
        return TechStack.forNew(
                TechStackName.of("Spring Boot 3.5"),
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.empty(),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                FIXED_CLOCK.instant());
    }

    /** 기존 TechStack Fixture (저장된 상태) */
    public static TechStack defaultExistingTechStack() {
        Instant now = FIXED_CLOCK.instant();
        return TechStack.of(
                TechStackId.of(1L),
                TechStackName.of("Spring Boot 3.5"),
                TechStackStatus.ACTIVE,
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.empty(),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 활성 상태 TechStack */
    public static TechStack activeTechStack() {
        return defaultExistingTechStack();
    }

    /** 비권장 상태 TechStack */
    public static TechStack deprecatedTechStack() {
        Instant now = FIXED_CLOCK.instant();
        TechStack techStack =
                TechStack.of(
                        TechStackId.of(2L),
                        TechStackName.of("Spring Boot 2.7"),
                        TechStackStatus.DEPRECATED,
                        LanguageType.JAVA,
                        LanguageVersion.of("17"),
                        LanguageFeatures.empty(),
                        FrameworkType.SPRING_BOOT,
                        FrameworkVersion.of("2.7.18"),
                        FrameworkModules.empty(),
                        PlatformType.BACKEND,
                        RuntimeEnvironment.JVM,
                        BuildToolType.GRADLE,
                        BuildConfigFile.of("build.gradle"),
                        ReferenceLinks.empty(),
                        DeletionStatus.active(),
                        now,
                        now);
        return techStack;
    }

    /** 보관 상태 TechStack */
    public static TechStack archivedTechStack() {
        Instant now = FIXED_CLOCK.instant();
        TechStack techStack =
                TechStack.of(
                        TechStackId.of(3L),
                        TechStackName.of("Spring Boot 2.5"),
                        TechStackStatus.ARCHIVED,
                        LanguageType.JAVA,
                        LanguageVersion.of("11"),
                        LanguageFeatures.empty(),
                        FrameworkType.SPRING_BOOT,
                        FrameworkVersion.of("2.5.15"),
                        FrameworkModules.empty(),
                        PlatformType.BACKEND,
                        RuntimeEnvironment.JVM,
                        BuildToolType.GRADLE,
                        BuildConfigFile.of("build.gradle"),
                        ReferenceLinks.empty(),
                        DeletionStatus.active(),
                        now,
                        now);
        return techStack;
    }

    /** 삭제된 TechStack */
    public static TechStack deletedTechStack() {
        Instant now = FIXED_CLOCK.instant();
        TechStack techStack =
                TechStack.of(
                        TechStackId.of(4L),
                        TechStackName.of("Deleted TechStack"),
                        TechStackStatus.ACTIVE,
                        LanguageType.JAVA,
                        LanguageVersion.of("21"),
                        LanguageFeatures.empty(),
                        FrameworkType.SPRING_BOOT,
                        FrameworkVersion.of("3.5.0"),
                        FrameworkModules.empty(),
                        PlatformType.BACKEND,
                        RuntimeEnvironment.JVM,
                        BuildToolType.GRADLE,
                        BuildConfigFile.of("build.gradle"),
                        ReferenceLinks.empty(),
                        DeletionStatus.deletedAt(now),
                        now,
                        now);
        return techStack;
    }

    /** 언어 기능이 포함된 TechStack */
    public static TechStack techStackWithLanguageFeatures() {
        Instant now = FIXED_CLOCK.instant();
        return TechStack.of(
                TechStackId.of(5L),
                TechStackName.of("Java 21 with Features"),
                TechStackStatus.ACTIVE,
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.of(
                        java.util.List.of("VIRTUAL_THREAD", "RECORD", "PATTERN_MATCHING")),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 프레임워크 모듈이 포함된 TechStack */
    public static TechStack techStackWithFrameworkModules() {
        Instant now = FIXED_CLOCK.instant();
        return TechStack.of(
                TechStackId.of(6L),
                TechStackName.of("Spring Boot with Modules"),
                TechStackStatus.ACTIVE,
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.empty(),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.of(java.util.List.of("WEB", "JPA", "SECURITY", "ACTUATOR")),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 참조 링크가 포함된 TechStack */
    public static TechStack techStackWithReferenceLinks() {
        Instant now = FIXED_CLOCK.instant();
        return TechStack.of(
                TechStackId.of(8L),
                TechStackName.of("Spring Boot with References"),
                TechStackStatus.ACTIVE,
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.empty(),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.of(
                        java.util.List.of(
                                "https://docs.spring.io/spring-boot/docs/current/reference/html/",
                                "https://spring.io/guides")),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 TechStack 생성 */
    public static TechStack customTechStack(
            Long id,
            String name,
            TechStackStatus status,
            LanguageType languageType,
            String languageVersion,
            FrameworkType frameworkType,
            String frameworkVersion,
            Instant createdAt,
            Instant updatedAt) {
        return TechStack.of(
                TechStackId.of(id),
                TechStackName.of(name),
                status,
                languageType,
                LanguageVersion.of(languageVersion),
                LanguageFeatures.empty(),
                frameworkType,
                FrameworkVersion.of(frameworkVersion),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }

    /** 특정 상태의 TechStack 생성 */
    public static TechStack techStackWithStatus(TechStackStatus status) {
        Instant now = FIXED_CLOCK.instant();
        return TechStack.of(
                TechStackId.of(7L),
                TechStackName.of("Custom Status TechStack"),
                status,
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.empty(),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }
}
