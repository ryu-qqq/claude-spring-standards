package com.ryuqq.application.mcp.dto.response;

/**
 * LayerValidationStatsResult - 레이어별 검증 통계 결과
 *
 * @param zeroTolerance Zero-Tolerance 규칙 개수
 * @param checklist 체크리스트 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerValidationStatsResult(int zeroTolerance, int checklist) {}
