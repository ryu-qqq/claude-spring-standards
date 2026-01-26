package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * RuleContextResult - 규칙 컨텍스트 결과
 *
 * @param conventions 컨벤션 목록 (코딩 규칙 포함)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleContextResult(List<ConventionWithRulesResult> conventions) {}
