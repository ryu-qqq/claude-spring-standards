package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * RuleExampleRow - RuleExample DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param ruleId 규칙 ID
 * @param exampleType 예제 타입 (GOOD/BAD)
 * @param code 코드 예제
 * @param explanation 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleRow(Long ruleId, String exampleType, String code, String explanation) {}
