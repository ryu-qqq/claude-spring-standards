package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * ConventionWithRulesResult - 규칙을 포함한 컨벤션 결과
 *
 * @param id 컨벤션 ID
 * @param name 컨벤션 이름
 * @param description 설명
 * @param codingRules 코딩 규칙 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConventionWithRulesResult(
        Long id, String name, String description, List<CodingRuleWithDetailsResult> codingRules) {}
