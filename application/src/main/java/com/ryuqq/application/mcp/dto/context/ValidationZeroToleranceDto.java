package com.ryuqq.application.mcp.dto.context;

import java.util.List;

/**
 * ValidationZeroToleranceDto - Validation Context용 ZeroTolerance DTO
 *
 * <p>Persistence Layer의 ValidationZeroToleranceRow를 Application Layer로 변환합니다.
 *
 * @param layerCode 레이어 코드
 * @param ruleCode 규칙 코드
 * @param ruleName 규칙 이름
 * @param appliesTo 적용 대상 목록
 * @param severity 심각도
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 유형
 * @param autoRejectPr PR 자동 거부 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationZeroToleranceDto(
        String layerCode,
        String ruleCode,
        String ruleName,
        List<String> appliesTo,
        String severity,
        String detectionPattern,
        String detectionType,
        boolean autoRejectPr) {}
