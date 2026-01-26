package com.ryuqq.application.convention.dto.command;

/**
 * CreateConventionCommand - Convention 생성 Command DTO
 *
 * <p>Convention 생성에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-002: 생성용은 Create{Domain}Command 네이밍.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param moduleId 레이어 ID (Layer 테이블 FK)
 * @param version 컨벤션 버전 (예: "1.0.0")
 * @param description 컨벤션 설명
 * @author ryu-qqq
 */
public record CreateConventionCommand(Long moduleId, String version, String description) {}
