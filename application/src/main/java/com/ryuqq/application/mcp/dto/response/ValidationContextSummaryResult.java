package com.ryuqq.application.mcp.dto.response;

import java.util.Map;

/**
 * ValidationContextSummaryResult - Validation Context 요약 정보
 *
 * @param totalZeroTolerance 전체 Zero-Tolerance 규칙 개수
 * @param totalChecklist 전체 체크리스트 개수
 * @param autoCheckableCount 자동 체크 가능한 항목 개수
 * @param byLayer 레이어별 통계
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationContextSummaryResult(
        int totalZeroTolerance,
        int totalChecklist,
        int autoCheckableCount,
        Map<String, LayerValidationStatsResult> byLayer) {}
