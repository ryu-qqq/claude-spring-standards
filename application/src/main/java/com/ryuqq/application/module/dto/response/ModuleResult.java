package com.ryuqq.application.module.dto.response;

import java.time.Instant;

/**
 * ModuleResult - 모듈 조회 결과
 *
 * <p>모듈 도메인 데이터를 Application 레이어에서 전달하기 위한 DTO입니다.
 *
 * @param moduleId 모듈 ID
 * @param layerId 레이어 ID
 * @param parentModuleId 부모 모듈 ID (nullable)
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param modulePath 모듈 파일 시스템 경로
 * @param buildIdentifier 빌드 시스템 식별자 (nullable)
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 * @author ryu-qqq
 */
public record ModuleResult(
        Long moduleId,
        Long layerId,
        Long parentModuleId,
        String name,
        String description,
        String modulePath,
        String buildIdentifier,
        Instant createdAt,
        Instant updatedAt) {}
