package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ValidationChecklistRow - Validation Context용 ChecklistItem DTO
 *
 * <p>Layer → Module → Convention → CodingRule → ChecklistItem JOIN 결과를 담습니다.
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param layerCode 레이어 코드
 * @param ruleCode 규칙 코드
 * @param checkDescription 체크 설명
 * @param severity 심각도
 * @param automationTool 자동화 도구 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationChecklistRow(
        String layerCode,
        String ruleCode,
        String checkDescription,
        String severity,
        String automationTool) {}
