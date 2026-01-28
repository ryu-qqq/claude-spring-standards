package com.ryuqq.application.packagepurpose.dto.command;

/**
 * CreatePackagePurposeCommand - 패키지 목적 생성 커맨드
 *
 * <p>패키지 목적 생성에 필요한 데이터를 전달합니다.
 *
 * @param structureId 패키지 구조 ID
 * @param code 목적 코드 (예: AGGREGATE, VALUE_OBJECT)
 * @param name 목적 이름
 * @param description 설명 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreatePackagePurposeCommand(
        Long structureId, String code, String name, String description) {}
