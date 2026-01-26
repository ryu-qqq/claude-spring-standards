package com.ryuqq.application.mcp.dto.response;

/**
 * ArchitectureSummaryResult - 아키텍처 요약 정보
 *
 * @param id 아키텍처 ID
 * @param name 아키텍처 이름
 * @param description 아키텍처 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchitectureSummaryResult(Long id, String name, String description) {}
