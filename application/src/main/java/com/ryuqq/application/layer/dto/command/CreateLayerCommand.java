package com.ryuqq.application.layer.dto.command;

/**
 * CreateLayerCommand - Layer 생성 Command
 *
 * <p>Layer 생성 시 필요한 데이터를 담는 불변 객체입니다.
 *
 * <p>CDTO-006: Command 내부에서 Validation 금지 → REST API Layer에서 검증합니다.
 *
 * @param architectureId 아키텍처 ID
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명 (nullable)
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 */
public record CreateLayerCommand(
        Long architectureId, String code, String name, String description, int orderIndex) {}
