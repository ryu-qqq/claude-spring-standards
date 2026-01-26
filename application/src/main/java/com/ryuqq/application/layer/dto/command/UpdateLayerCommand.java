package com.ryuqq.application.layer.dto.command;

/**
 * UpdateLayerCommand - Layer 수정 Command
 *
 * <p>Layer 수정 시 필요한 데이터를 담는 불변 객체입니다.
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
 * @param id 수정 대상 Layer ID
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명 (nullable)
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateLayerCommand(
        Long id, String code, String name, String description, int orderIndex) {}
