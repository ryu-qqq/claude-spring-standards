package com.ryuqq.application.techstack.dto.command;

import java.util.List;

/**
 * CreateTechStackCommand - TechStack 생성 Command DTO
 *
 * <p>TechStack 생성에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-002: 생성용은 Create{Domain}Command 네이밍.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
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
 * @param referenceLinks 참조 링크 목록 (공식 문서, 튜토리얼 등)
 * @author ryu-qqq
 */
public record CreateTechStackCommand(
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
        List<String> referenceLinks) {}
