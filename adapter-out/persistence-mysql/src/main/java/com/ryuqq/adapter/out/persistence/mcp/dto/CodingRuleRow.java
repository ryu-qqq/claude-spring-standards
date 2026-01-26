package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * CodingRuleRow - CodingRule 기본 정보 DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param ruleId 규칙 ID
 * @param ruleCode 규칙 코드
 * @param ruleName 규칙 이름
 * @param ruleDescription 규칙 설명
 * @param severity 심각도
 * @param appliesTo 적용 대상
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleRow(
        Long ruleId,
        String ruleCode,
        String ruleName,
        String ruleDescription,
        String severity,
        String appliesTo) {}
