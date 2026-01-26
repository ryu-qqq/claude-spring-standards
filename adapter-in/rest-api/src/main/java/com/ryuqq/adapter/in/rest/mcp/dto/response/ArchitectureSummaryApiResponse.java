package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ArchitectureSummaryApiResponse - 아키텍처 요약 정보
 *
 * @param id 아키텍처 ID
 * @param name 아키텍처 이름
 * @param description 아키텍처 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "아키텍처 요약 정보")
public record ArchitectureSummaryApiResponse(
        @Schema(description = "아키텍처 ID", example = "1") Long id,
        @Schema(description = "아키텍처 이름", example = "Hexagonal Architecture") String name,
        @Schema(description = "아키텍처 설명", example = "포트-어댑터 패턴 기반 아키텍처") String description) {}
