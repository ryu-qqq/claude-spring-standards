package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * CodingRuleWithDetailsResult - 상세 정보를 포함한 코딩 규칙 결과
 *
 * @param id 규칙 ID
 * @param code 규칙 코드
 * @param title 규칙 제목
 * @param description 설명
 * @param severity 심각도
 * @param classType 클래스 타입
 * @param examples 예시 목록
 * @param zeroTolerance Zero-Tolerance 정보 (nullable)
 * @param checklistItem 체크리스트 항목 정보 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleWithDetailsResult(
        Long id,
        String code,
        String title,
        String description,
        String severity,
        String classType,
        List<RuleExampleDetailResult> examples,
        ZeroToleranceDetailResult zeroTolerance,
        ChecklistItemDetailResult checklistItem) {}
