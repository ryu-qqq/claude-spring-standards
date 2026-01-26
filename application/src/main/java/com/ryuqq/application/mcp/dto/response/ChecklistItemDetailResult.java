package com.ryuqq.application.mcp.dto.response;

/**
 * ChecklistItemDetailResult - 체크리스트 항목 상세 정보
 *
 * @param checkDescription 체크 설명
 * @param autoCheckable 자동 체크 가능 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemDetailResult(String checkDescription, boolean autoCheckable) {}
