package com.ryuqq.adapter.out.persistence.zerotolerance.entity;

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
 * ZeroToleranceRuleJpaEntity - Zero-Tolerance 규칙 JPA 엔티티
 *
 * <p>zero_tolerance_rule 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "zero_tolerance_rule")
public class ZeroToleranceRuleJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(name = "detection_pattern", length = 500, nullable = false)
    private String detectionPattern;

    @Column(name = "detection_type", length = 20, nullable = false)
    private String detectionType;

    @Column(name = "auto_reject_pr", nullable = false)
    private boolean autoRejectPr;

    @Column(name = "error_message", length = 500, nullable = false)
    private String errorMessage;

    protected ZeroToleranceRuleJpaEntity() {}

    private ZeroToleranceRuleJpaEntity(
            Long id,
            Long ruleId,
            String type,
            String detectionPattern,
            String detectionType,
            boolean autoRejectPr,
            String errorMessage,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.ruleId = ruleId;
        this.type = type;
        this.detectionPattern = detectionPattern;
        this.detectionType = detectionType;
        this.autoRejectPr = autoRejectPr;
        this.errorMessage = errorMessage;
    }

    public static ZeroToleranceRuleJpaEntity of(
            Long id,
            Long ruleId,
            String type,
            String detectionPattern,
            String detectionType,
            boolean autoRejectPr,
            String errorMessage,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ZeroToleranceRuleJpaEntity(
                id,
                ruleId,
                type,
                detectionPattern,
                detectionType,
                autoRejectPr,
                errorMessage,
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

    public String getType() {
        return type;
    }

    public String getDetectionPattern() {
        return detectionPattern;
    }

    public String getDetectionType() {
        return detectionType;
    }

    public boolean isAutoRejectPr() {
        return autoRejectPr;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZeroToleranceRuleJpaEntity that = (ZeroToleranceRuleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
