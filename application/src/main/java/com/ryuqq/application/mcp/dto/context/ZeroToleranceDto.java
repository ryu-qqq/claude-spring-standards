package com.ryuqq.application.mcp.dto.context;

/**
 * ZeroToleranceDto - Zero-Tolerance 규칙 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 타입
 * @param autoRejectPr PR 자동 거부 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceDto(
        String detectionPattern, String detectionType, boolean autoRejectPr) {}
