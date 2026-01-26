package com.ryuqq.application.checklistitem.dto.command;

/**
 * CreateChecklistItemCommand - 체크리스트 항목 생성 커맨드
 *
 * <p>체크리스트 항목 생성에 필요한 데이터를 전달합니다.
 *
 * @param ruleId 코딩 규칙 ID
 * @param sequenceOrder 순서
 * @param checkDescription 체크 설명
 * @param checkType 체크 타입 (AUTOMATED/MANUAL/SEMI_AUTO)
 * @param automationTool 자동화 도구 (nullable)
 * @param automationRuleId 자동화 규칙 ID (nullable)
 * @param critical 필수 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreateChecklistItemCommand(
        Long ruleId,
        int sequenceOrder,
        String checkDescription,
        String checkType,
        String automationTool,
        String automationRuleId,
        boolean critical) {}
