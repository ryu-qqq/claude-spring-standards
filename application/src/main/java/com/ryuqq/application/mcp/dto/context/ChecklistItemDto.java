package com.ryuqq.application.mcp.dto.context;

/**
 * ChecklistItemDto - 체크리스트 아이템 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param checkDescription 체크 설명
 * @param hasAutomation 자동화 도구 존재 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemDto(String checkDescription, boolean hasAutomation) {}
