package com.ryuqq.adapter.out.persistence.checklistitem.entity;

import com.ryuqq.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/**
 * ChecklistItemJpaEntity - 체크리스트 항목 JPA 엔티티
 *
 * <p>checklist_item 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "checklist_item")
public class ChecklistItemJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    @Column(name = "check_description", length = 500, nullable = false)
    private String checkDescription;

    @Column(name = "check_type", length = 20, nullable = false)
    private String checkType;

    @Column(name = "automation_tool", length = 50)
    private String automationTool;

    @Column(name = "automation_rule_id", length = 100)
    private String automationRuleId;

    @Column(name = "is_critical", nullable = false)
    private boolean isCritical;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "feedback_id")
    private Long feedbackId;

    protected ChecklistItemJpaEntity() {}

    private ChecklistItemJpaEntity(
            Long id,
            Long ruleId,
            int sequenceOrder,
            String checkDescription,
            String checkType,
            String automationTool,
            String automationRuleId,
            boolean isCritical,
            String source,
            Long feedbackId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.ruleId = ruleId;
        this.sequenceOrder = sequenceOrder;
        this.checkDescription = checkDescription;
        this.checkType = checkType;
        this.automationTool = automationTool;
        this.automationRuleId = automationRuleId;
        this.isCritical = isCritical;
        this.source = source;
        this.feedbackId = feedbackId;
    }

    public static ChecklistItemJpaEntity of(
            Long id,
            Long ruleId,
            int sequenceOrder,
            String checkDescription,
            String checkType,
            String automationTool,
            String automationRuleId,
            boolean isCritical,
            String source,
            Long feedbackId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ChecklistItemJpaEntity(
                id,
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                isCritical,
                source,
                feedbackId,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public String getCheckDescription() {
        return checkDescription;
    }

    public String getCheckType() {
        return checkType;
    }

    public String getAutomationTool() {
        return automationTool;
    }

    public String getAutomationRuleId() {
        return automationRuleId;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public String getSource() {
        return source;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChecklistItemJpaEntity that = (ChecklistItemJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
