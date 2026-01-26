package com.ryuqq.application.checklistitem.dto.command;

/**
 * UpdateChecklistItemCommand - 체크리스트 항목 수정 커맨드
 *
 * <p>체크리스트 항목 수정에 필요한 데이터를 전달합니다. Nullable 필드는 수정하지 않음을 의미합니다.
 *
 * @param checklistItemId 수정할 체크리스트 항목 ID
 * @param sequenceOrder 순서 (nullable)
 * @param checkDescription 체크 설명 (nullable)
 * @param checkType 체크 타입 (nullable)
 * @param automationTool 자동화 도구 (nullable)
 * @param automationRuleId 자동화 규칙 ID (nullable)
 * @param critical 필수 여부 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateChecklistItemCommand(
        Long checklistItemId,
        Integer sequenceOrder,
        String checkDescription,
        String checkType,
        String automationTool,
        String automationRuleId,
        Boolean critical) {}
