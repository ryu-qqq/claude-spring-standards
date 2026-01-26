package com.ryuqq.application.mcp.dto.response;

/**
 * ChecklistItemResult - 체크리스트 항목 결과
 *
 * @param ruleCode 규칙 코드
 * @param checkDescription 체크 설명
 * @param severity 심각도
 * @param autoCheckable 자동 체크 가능 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemResult(
        String ruleCode, String checkDescription, String severity, boolean autoCheckable) {}
