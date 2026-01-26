package com.ryuqq.application.mcp.dto.response;

/**
 * ZeroToleranceDetailResult - Zero-Tolerance 상세 정보
 *
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 타입
 * @param autoRejectPr PR 자동 거부 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceDetailResult(
        String detectionPattern, String detectionType, boolean autoRejectPr) {}
