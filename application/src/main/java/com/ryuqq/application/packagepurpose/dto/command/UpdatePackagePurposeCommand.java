package com.ryuqq.application.packagepurpose.dto.command;

/**
 * UpdatePackagePurposeCommand - 패키지 목적 수정 커맨드
 *
 * <p>패키지 목적 수정에 필요한 데이터를 전달합니다.
 *
 * @param packagePurposeId 패키지 목적 ID
 * @param code 목적 코드
 * @param name 목적 이름
 * @param description 설명 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdatePackagePurposeCommand(
        Long packagePurposeId, String code, String name, String description) {}
