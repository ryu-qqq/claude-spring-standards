package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * ZeroToleranceRuleResult - Zero-Tolerance 규칙 결과
 *
 * @param ruleCode 규칙 코드
 * @param ruleTitle 규칙 제목
 * @param layer 레이어
 * @param classTypes 클래스 타입 목록
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 타입
 * @param autoRejectPr PR 자동 거부 여부
 * @param message 에러 메시지
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceRuleResult(
        String ruleCode,
        String ruleTitle,
        String layer,
        List<String> classTypes,
        String detectionPattern,
        String detectionType,
        boolean autoRejectPr,
        String message) {}
