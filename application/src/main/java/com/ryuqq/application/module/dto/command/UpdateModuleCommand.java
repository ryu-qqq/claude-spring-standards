package com.ryuqq.application.module.dto.command;

/**
 * UpdateModuleCommand - 모듈 수정 커맨드
 *
 * <p>모듈 수정에 필요한 데이터를 전달합니다.
 *
 * <p>Layer는 생성 시점에 결정되므로 수정 대상에서 제외됩니다.
 *
 * @param moduleId 수정할 모듈 ID
 * @param parentModuleId 부모 모듈 ID (nullable)
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param modulePath 모듈 파일 시스템 경로
 * @param buildIdentifier 빌드 시스템 식별자 (nullable)
 * @author ryu-qqq
 */
public record UpdateModuleCommand(
        Long moduleId,
        Long parentModuleId,
        String name,
        String description,
        String modulePath,
        String buildIdentifier) {}
