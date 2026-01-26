package com.ryuqq.adapter.in.rest.techstack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * CreateTechStackApiRequest - TechStack 생성 API Request
 *
 * <p>TechStack 생성 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param name TechStack 이름
 * @param languageType 언어 타입 (JAVA, KOTLIN, PYTHON 등)
 * @param languageVersion 언어 버전 (예: "21", "17")
 * @param languageFeatures 언어 기능 목록 (예: ["records", "sealed-classes"])
 * @param frameworkType 프레임워크 타입 (SPRING_BOOT, DJANGO 등)
 * @param frameworkVersion 프레임워크 버전 (예: "3.5.0")
 * @param frameworkModules 프레임워크 모듈 목록 (예: ["spring-web", "spring-data-jpa"])
 * @param platformType 플랫폼 타입 (JVM, NATIVE 등)
 * @param runtimeEnvironment 런타임 환경 (예: "JVM")
 * @param buildToolType 빌드 도구 타입 (GRADLE, MAVEN 등)
 * @param buildConfigFile 빌드 설정 파일 경로 (예: "build.gradle")
 * @author ryu-qqq
 */
@Schema(description = "TechStack 생성 요청")
public record CreateTechStackApiRequest(
        @Schema(description = "TechStack 이름", example = "Spring Boot 3.5.x + Java 21")
                @NotBlank(message = "name은 필수입니다")
                @Size(max = 100, message = "name은 100자 이내여야 합니다")
                String name,
        @Schema(description = "언어 타입", example = "JAVA") @NotBlank(message = "languageType은 필수입니다")
                String languageType,
        @Schema(description = "언어 버전", example = "21")
                @NotBlank(message = "languageVersion은 필수입니다")
                @Size(max = 20, message = "languageVersion은 20자 이내여야 합니다")
                String languageVersion,
        @Schema(
                        description = "언어 기능 목록",
                        example = "[\"records\", \"sealed-classes\", \"pattern-matching\"]")
                @NotNull(message = "languageFeatures는 필수입니다")
                List<String> languageFeatures,
        @Schema(description = "프레임워크 타입", example = "SPRING_BOOT")
                @NotBlank(message = "frameworkType은 필수입니다")
                String frameworkType,
        @Schema(description = "프레임워크 버전", example = "3.5.0")
                @NotBlank(message = "frameworkVersion은 필수입니다")
                @Size(max = 20, message = "frameworkVersion은 20자 이내여야 합니다")
                String frameworkVersion,
        @Schema(description = "프레임워크 모듈 목록", example = "[\"spring-web\", \"spring-data-jpa\"]")
                @NotNull(message = "frameworkModules는 필수입니다")
                List<String> frameworkModules,
        @Schema(description = "플랫폼 타입", example = "JVM") @NotBlank(message = "platformType은 필수입니다")
                String platformType,
        @Schema(description = "런타임 환경", example = "JVM")
                @NotBlank(message = "runtimeEnvironment는 필수입니다")
                @Size(max = 50, message = "runtimeEnvironment는 50자 이내여야 합니다")
                String runtimeEnvironment,
        @Schema(description = "빌드 도구 타입", example = "GRADLE")
                @NotBlank(message = "buildToolType은 필수입니다")
                String buildToolType,
        @Schema(description = "빌드 설정 파일 경로", example = "build.gradle")
                @NotBlank(message = "buildConfigFile은 필수입니다")
                @Size(max = 100, message = "buildConfigFile은 100자 이내여야 합니다")
                String buildConfigFile,
        @Schema(
                        description = "참조 링크 목록",
                        example = "[\"https://docs.spring.io\", \"https://spring.io/guides\"]",
                        nullable = true)
                List<String> referenceLinks) {}
