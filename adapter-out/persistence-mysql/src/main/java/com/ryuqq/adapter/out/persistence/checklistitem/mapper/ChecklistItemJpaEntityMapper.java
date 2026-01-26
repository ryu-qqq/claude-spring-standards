package com.ryuqq.adapter.out.persistence.checklistitem.mapper;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.AutomationRuleId;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckDescription;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.ChecklistSource;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemJpaEntityMapper - ChecklistItem Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p>EMAP-002: Pure Java만 사용 (Lombok/MapStruct 금지)
 *
 * <p>EMAP-003: 시간 필드 생성 금지 (Instant.now() 금지)
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemJpaEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public ChecklistItemJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return ChecklistItem 도메인 객체
     */
    public ChecklistItem toDomain(ChecklistItemJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ChecklistItem.reconstitute(
                ChecklistItemId.of(entity.getId()),
                CodingRuleId.of(entity.getRuleId()),
                SequenceOrder.of(entity.getSequenceOrder()),
                CheckDescription.of(entity.getCheckDescription()),
                mapCheckType(entity.getCheckType()),
                mapAutomationTool(entity.getAutomationTool()),
                mapAutomationRuleId(entity.getAutomationRuleId()),
                entity.isCritical(),
                mapChecklistSource(entity.getSource()),
                entity.getFeedbackId(),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>EMAP-004: toEntity(Domain) 메서드 필수
     *
     * <p>EMAP-006: Entity.of() 호출
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain ChecklistItem 도메인 객체
     * @return JPA 엔티티
     */
    public ChecklistItemJpaEntity toEntity(ChecklistItem domain) {
        if (domain == null) {
            return null;
        }
        return ChecklistItemJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.ruleIdValue(),
                domain.sequenceOrderValue(),
                domain.checkDescriptionValue(),
                domain.checkTypeName(),
                domain.automationToolName(),
                domain.automationRuleIdValue(),
                domain.isCritical(),
                domain.sourceName(),
                domain.feedbackId(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * CheckType 문자열 -> Enum 변환
     *
     * @param checkType 체크 타입 문자열
     * @return CheckType enum
     */
    private CheckType mapCheckType(String checkType) {
        if (checkType == null || checkType.isBlank()) {
            throw new IllegalArgumentException("CheckType must not be null or blank");
        }
        return CheckType.valueOf(checkType);
    }

    /**
     * AutomationTool 문자열 -> Enum 변환
     *
     * @param automationTool 자동화 도구 문자열 (nullable)
     * @return AutomationTool enum 또는 null
     */
    private AutomationTool mapAutomationTool(String automationTool) {
        if (automationTool == null || automationTool.isBlank()) {
            return null;
        }
        return AutomationTool.valueOf(automationTool);
    }

    /**
     * AutomationRuleId 문자열 -> VO 변환
     *
     * @param automationRuleId 자동화 규칙 ID 문자열 (nullable)
     * @return AutomationRuleId VO
     */
    private AutomationRuleId mapAutomationRuleId(String automationRuleId) {
        if (automationRuleId == null || automationRuleId.isBlank()) {
            return AutomationRuleId.empty();
        }
        return AutomationRuleId.of(automationRuleId);
    }

    /**
     * ChecklistSource 문자열 -> Enum 변환
     *
     * @param source 소스 문자열 (nullable)
     * @return ChecklistSource enum 또는 MANUAL
     */
    private ChecklistSource mapChecklistSource(String source) {
        if (source == null || source.isBlank()) {
            return ChecklistSource.MANUAL;
        }
        return ChecklistSource.valueOf(source);
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(ChecklistItemJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
