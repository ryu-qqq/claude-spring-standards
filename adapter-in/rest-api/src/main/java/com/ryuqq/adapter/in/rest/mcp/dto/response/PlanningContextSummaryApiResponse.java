package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PlanningContextSummaryApiResponse - Planning Context 요약 정보
 *
 * @param totalModules 전체 모듈 개수
 * @param totalPackages 전체 패키지 개수
 * @param totalTemplates 전체 템플릿 개수
 * @param totalRules 전체 규칙 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Planning Context 요약 정보")
public record PlanningContextSummaryApiResponse(
        @Schema(description = "전체 모듈 개수", example = "4") int totalModules,
        @Schema(description = "전체 패키지 개수", example = "15") int totalPackages,
        @Schema(description = "전체 템플릿 개수", example = "25") int totalTemplates,
        @Schema(description = "전체 규칙 개수", example = "68") int totalRules) {}
