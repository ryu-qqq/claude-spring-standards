package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * ValidationContextResult - Validation Context 조회 결과
 *
 * <p>코드 검증에 필요한 Zero-Tolerance + Checklist 정보를 담습니다.
 *
 * @param zeroToleranceRules Zero-Tolerance 규칙 목록
 * @param checklist 체크리스트 항목 목록
 * @param summary 요약 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationContextResult(
        List<ZeroToleranceRuleResult> zeroToleranceRules,
        List<ChecklistItemResult> checklist,
        ValidationContextSummaryResult summary) {}
