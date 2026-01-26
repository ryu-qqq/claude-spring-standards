package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ZeroToleranceRow - ZeroToleranceRule DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param ruleId 규칙 ID
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 유형
 * @param autoRejectPr PR 자동 거부 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceRow(
        Long ruleId, String detectionPattern, String detectionType, boolean autoRejectPr) {}
