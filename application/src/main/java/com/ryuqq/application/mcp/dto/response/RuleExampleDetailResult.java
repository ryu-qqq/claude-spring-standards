package com.ryuqq.application.mcp.dto.response;

/**
 * RuleExampleDetailResult - 규칙 예시 상세 정보
 *
 * @param type 예시 타입 (GOOD/BAD)
 * @param code 예시 코드
 * @param explanation 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleDetailResult(String type, String code, String explanation) {}
