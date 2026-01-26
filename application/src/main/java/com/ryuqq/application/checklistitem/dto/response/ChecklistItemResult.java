package com.ryuqq.application.checklistitem.dto.response;

import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import java.time.Instant;

/**
 * ChecklistItemResult - 체크리스트 항목 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 체크리스트 항목 ID
 * @param ruleId 코딩 규칙 ID
 * @param sequenceOrder 순서
 * @param checkDescription 체크 설명
 * @param checkType 체크 타입 (AUTOMATED/MANUAL/SEMI_AUTO)
 * @param automationTool 자동화 도구 (nullable)
 * @param automationRuleId 자동화 규칙 ID (nullable)
 * @param critical 필수 여부
 * @param source 체크리스트 소스 (MANUAL/AGENT_FEEDBACK)
 * @param feedbackId 피드백 ID (nullable)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemResult(
        Long id,
        Long ruleId,
        int sequenceOrder,
        String checkDescription,
        String checkType,
        String automationTool,
        String automationRuleId,
        boolean critical,
        String source,
        Long feedbackId,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param checklistItem ChecklistItem 도메인 객체
     * @return ChecklistItemResult
     */
    public static ChecklistItemResult from(ChecklistItem checklistItem) {
        return new ChecklistItemResult(
                checklistItem.id().value(),
                checklistItem.ruleId().value(),
                checklistItem.sequenceOrder().value(),
                checklistItem.checkDescription().value(),
                checklistItem.checkType().name(),
                checklistItem.automationTool() != null
                        ? checklistItem.automationTool().name()
                        : null,
                checklistItem.automationRuleId() != null
                                && !checklistItem.automationRuleId().isEmpty()
                        ? checklistItem.automationRuleId().value()
                        : null,
                checklistItem.isCritical(),
                checklistItem.source().name(),
                checklistItem.feedbackId(),
                checklistItem.createdAt(),
                checklistItem.updatedAt());
    }
}
