package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ValidationZeroToleranceRow - Validation Context용 ZeroTolerance DTO
 *
 * <p>Layer → Module → Convention → CodingRule → ZeroToleranceRule JOIN 결과를 담습니다.
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param layerCode 레이어 코드
 * @param ruleCode 규칙 코드
 * @param ruleName 규칙 이름
 * @param appliesTo 적용 대상 (comma separated)
 * @param severity 심각도
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 유형
 * @param autoRejectPr PR 자동 거부 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationZeroToleranceRow(
        String layerCode,
        String ruleCode,
        String ruleName,
        String appliesTo,
        String severity,
        String detectionPattern,
        String detectionType,
        boolean autoRejectPr) {}
