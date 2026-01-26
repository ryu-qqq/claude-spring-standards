package com.ryuqq.application.techstack.dto.command;

import java.util.List;

/**
 * UpdateTechStackCommand - TechStack 수정 Command DTO
 *
 * <p>TechStack 수정에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-003: 수정용은 Update{Domain}Command 네이밍.
 *
 * <p>CDTO-004: Update Command는 UpdateData 생성에 필요한 전체 필드 포함.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
 *
 * @param id 수정 대상 TechStack ID
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
 * @param referenceLinks 참조 링크 목록
 * @author ryu-qqq
 */
public record UpdateTechStackCommand(
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
        List<String> referenceLinks) {}
