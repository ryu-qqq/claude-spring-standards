package com.ryuqq.application.packagestructure.dto.command;

/**
 * CreatePackageStructureCommand - 패키지 구조 생성 커맨드
 *
 * <p>패키지 구조 생성에 필요한 데이터를 전달합니다.
 *
 * @param moduleId 모듈 ID
 * @param pathPattern 경로 패턴
 * @param description 설명 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreatePackageStructureCommand(
        Long moduleId, String pathPattern, String description) {}
