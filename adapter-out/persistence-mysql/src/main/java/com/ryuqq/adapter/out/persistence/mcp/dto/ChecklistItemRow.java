package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ChecklistItemRow - ChecklistItem DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param ruleId 규칙 ID
 * @param checkDescription 체크 설명
 * @param automationTool 자동화 도구 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemRow(Long ruleId, String checkDescription, String automationTool) {}
