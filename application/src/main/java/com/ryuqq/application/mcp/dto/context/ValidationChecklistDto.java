package com.ryuqq.application.mcp.dto.context;

/**
 * ValidationChecklistDto - Validation Context용 ChecklistItem DTO
 *
 * <p>Persistence Layer의 ValidationChecklistRow를 Application Layer로 변환합니다.
 *
 * @param layerCode 레이어 코드
 * @param ruleCode 규칙 코드
 * @param checkDescription 체크 설명
 * @param severity 심각도
 * @param hasAutomation 자동화 도구 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationChecklistDto(
        String layerCode,
        String ruleCode,
        String checkDescription,
        String severity,
        boolean hasAutomation) {}
