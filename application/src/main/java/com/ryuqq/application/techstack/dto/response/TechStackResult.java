package com.ryuqq.application.techstack.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * TechStackResult - TechStack 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 TechStack 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지 → from(Domain) 메서드 금지.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
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
 * @param referenceLinks 참조 링크 목록
 * @param deleted 삭제 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record TechStackResult(
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
        List<String> referenceLinks,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt) {}
