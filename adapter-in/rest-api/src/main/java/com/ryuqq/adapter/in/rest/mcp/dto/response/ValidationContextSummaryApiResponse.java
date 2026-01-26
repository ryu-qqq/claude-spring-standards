package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * ValidationContextSummaryApiResponse - Validation Context 요약 정보
 *
 * @param totalZeroTolerance 전체 Zero-Tolerance 규칙 개수
 * @param totalChecklist 전체 체크리스트 개수
 * @param autoCheckableCount 자동 체크 가능한 항목 개수
 * @param byLayer 레이어별 통계
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Validation Context 요약 정보")
public record ValidationContextSummaryApiResponse(
        @Schema(description = "전체 Zero-Tolerance 규칙 개수", example = "10") int totalZeroTolerance,
        @Schema(description = "전체 체크리스트 개수", example = "45") int totalChecklist,
        @Schema(description = "자동 체크 가능한 항목 개수", example = "32") int autoCheckableCount,
        @Schema(description = "레이어별 통계") Map<String, LayerValidationStatsApiResponse> byLayer) {}
