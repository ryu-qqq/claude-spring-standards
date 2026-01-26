package com.ryuqq.application.mcp.dto.context;

import java.util.List;

/**
 * CodingRuleWithDetailsDto - CodingRule + 상세 정보 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param ruleId 규칙 ID
 * @param ruleCode 규칙 코드
 * @param ruleName 규칙 이름
 * @param ruleDescription 규칙 설명
 * @param severity 심각도
 * @param appliesTo 적용 대상
 * @param examples 규칙 예제 목록
 * @param zeroTolerance Zero-Tolerance 정보
 * @param checklistItem 체크리스트 아이템 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleWithDetailsDto(
        Long ruleId,
        String ruleCode,
        String ruleName,
        String ruleDescription,
        String severity,
        String appliesTo,
        List<RuleExampleDto> examples,
        ZeroToleranceDto zeroTolerance,
        ChecklistItemDto checklistItem) {}
