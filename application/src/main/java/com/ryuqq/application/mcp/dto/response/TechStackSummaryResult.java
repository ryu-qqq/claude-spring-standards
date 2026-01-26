package com.ryuqq.application.mcp.dto.response;

/**
 * TechStackSummaryResult - 기술 스택 요약 정보
 *
 * @param id 기술 스택 ID
 * @param name 기술 스택 이름
 * @param description 기술 스택 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record TechStackSummaryResult(Long id, String name, String description) {}
