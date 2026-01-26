package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LayerValidationStatsApiResponse - 레이어별 검증 통계 응답
 *
 * @param zeroTolerance Zero-Tolerance 규칙 개수
 * @param checklist 체크리스트 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "레이어별 검증 통계")
public record LayerValidationStatsApiResponse(
        @Schema(description = "Zero-Tolerance 규칙 개수", example = "5") int zeroTolerance,
        @Schema(description = "체크리스트 개수", example = "20") int checklist) {}
