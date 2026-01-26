package com.ryuqq.adapter.in.rest.techstack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * TechStackApiResponse - TechStack 조회 API Response
 *
 * <p>TechStack 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param id TechStack ID
 * @param name TechStack 이름
 * @param status 상태 (ACTIVE, DEPRECATED, ARCHIVED)
 * @param languageType 언어 타입
 * @param languageVersion 언어 버전
 * @param languageFeatures 언어 기능 목록
 * @param frameworkType 프레임워크 타입
 * @param frameworkVersion 프레임워크 버전
 * @param frameworkModules 프레임워크 모듈 목록
 * @param platformType 플랫폼 타입
 * @param runtimeEnvironment 런타임 환경
 * @param buildToolType 빌드 도구 타입
 * @param buildConfigFile 빌드 설정 파일
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 */
@Schema(description = "TechStack 조회 응답")
public record TechStackApiResponse(
        @Schema(description = "TechStack ID", example = "1") Long id,
        @Schema(description = "TechStack 이름", example = "Spring Boot 3.5") String name,
        @Schema(description = "상태", example = "ACTIVE") String status,
        @Schema(description = "언어 타입", example = "JAVA") String languageType,
        @Schema(description = "언어 버전", example = "21") String languageVersion,
        @Schema(description = "언어 기능 목록", example = "[\"RECORDS\", \"SEALED_CLASSES\"]")
                List<String> languageFeatures,
        @Schema(description = "프레임워크 타입", example = "SPRING_BOOT") String frameworkType,
        @Schema(description = "프레임워크 버전", example = "3.5.0") String frameworkVersion,
        @Schema(description = "프레임워크 모듈 목록", example = "[\"spring-web\", \"spring-data-jpa\"]")
                List<String> frameworkModules,
        @Schema(description = "플랫폼 타입", example = "BACKEND") String platformType,
        @Schema(description = "런타임 환경", example = "JVM") String runtimeEnvironment,
        @Schema(description = "빌드 도구 타입", example = "GRADLE") String buildToolType,
        @Schema(description = "빌드 설정 파일", example = "build.gradle.kts") String buildConfigFile,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
