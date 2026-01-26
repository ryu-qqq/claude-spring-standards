package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModuleContextSummaryApiResponse - Module Context 요약 정보
 *
 * @param packageCount 패키지 개수
 * @param templateCount 템플릿 개수
 * @param ruleCount 규칙 개수
 * @param zeroToleranceCount Zero-Tolerance 규칙 개수
 * @param archTestCount ArchUnit 테스트 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Module Context 요약 정보")
public record ModuleContextSummaryApiResponse(
        @Schema(description = "패키지 개수", example = "5") int packageCount,
        @Schema(description = "템플릿 개수", example = "12") int templateCount,
        @Schema(description = "규칙 개수", example = "23") int ruleCount,
        @Schema(description = "Zero-Tolerance 규칙 개수", example = "5") int zeroToleranceCount,
        @Schema(description = "ArchUnit 테스트 개수", example = "8") int archTestCount) {}
