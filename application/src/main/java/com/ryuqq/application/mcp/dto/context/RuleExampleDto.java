package com.ryuqq.application.mcp.dto.context;

/**
 * RuleExampleDto - 규칙 예제 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param exampleType 예제 타입 (GOOD/BAD)
 * @param code 예제 코드
 * @param explanation 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleDto(String exampleType, String code, String explanation) {}
