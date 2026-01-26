package com.ryuqq.application.module.dto.command;

/**
 * CreateModuleCommand - 모듈 생성 커맨드
 *
 * <p>모듈 생성에 필요한 데이터를 전달합니다.
 *
 * @param layerId 레이어 ID
 * @param parentModuleId 부모 모듈 ID (nullable, 루트 모듈일 경우 null)
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param modulePath 모듈 파일 시스템 경로 (예: domain, adapter-in/rest-api)
 * @param buildIdentifier 빌드 시스템 식별자 (예: :domain, :adapter-in:rest-api) (nullable)
 * @author ryu-qqq
 */
public record CreateModuleCommand(
        Long layerId,
        Long parentModuleId,
        String name,
        String description,
        String modulePath,
        String buildIdentifier) {}
