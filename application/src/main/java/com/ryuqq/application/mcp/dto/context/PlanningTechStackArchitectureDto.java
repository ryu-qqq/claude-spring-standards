package com.ryuqq.application.mcp.dto.context;

/**
 * PlanningTechStackArchitectureDto - TechStack + Architecture 조회 결과
 *
 * <p>MCP Planning Context 조회용 DTO입니다.
 *
 * @param techStackId 기술 스택 ID
 * @param techStackName 기술 스택 이름
 * @param languageType 언어 타입
 * @param languageVersion 언어 버전
 * @param frameworkType 프레임워크 타입
 * @param frameworkVersion 프레임워크 버전
 * @param architectureId 아키텍처 ID
 * @param architectureName 아키텍처 이름
 * @param patternDescription 패턴 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PlanningTechStackArchitectureDto(
        Long techStackId,
        String techStackName,
        String languageType,
        String languageVersion,
        String frameworkType,
        String frameworkVersion,
        Long architectureId,
        String architectureName,
        String patternDescription) {}
