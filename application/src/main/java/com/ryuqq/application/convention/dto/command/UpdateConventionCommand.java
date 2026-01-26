package com.ryuqq.application.convention.dto.command;

/**
 * UpdateConventionCommand - Convention 수정 Command DTO
 *
 * <p>Convention 수정에 필요한 데이터를 담습니다.
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
 * @param id 수정 대상 Convention ID
 * @param moduleId 모듈 ID (Module 테이블 FK)
 * @param version 컨벤션 버전 (예: "1.0.0")
 * @param description 컨벤션 설명
 * @param active 활성화 여부
 * @author ryu-qqq
 */
public record UpdateConventionCommand(
        Long id, Long moduleId, String version, String description, boolean active) {}
