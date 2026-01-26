package com.ryuqq.adapter.out.persistence.ruleexample.entity;

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
 * RuleExampleJpaEntity - 규칙 예시 JPA 엔티티
 *
 * <p>rule_example 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "rule_example")
public class RuleExampleJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    @Column(name = "example_type", length = 10, nullable = false)
    private String exampleType;

    @Column(name = "code", columnDefinition = "TEXT", nullable = false)
    private String code;

    @Column(name = "language", length = 20, nullable = false)
    private String language;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "highlight_lines", columnDefinition = "JSON")
    private String highlightLines;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "feedback_id")
    private Long feedbackId;

    protected RuleExampleJpaEntity() {}

    private RuleExampleJpaEntity(
            Long id,
            Long ruleId,
            String exampleType,
            String code,
            String language,
            String explanation,
            String highlightLines,
            String source,
            Long feedbackId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.ruleId = ruleId;
        this.exampleType = exampleType;
        this.code = code;
        this.language = language;
        this.explanation = explanation;
        this.highlightLines = highlightLines;
        this.source = source;
        this.feedbackId = feedbackId;
    }

    public static RuleExampleJpaEntity of(
            Long id,
            Long ruleId,
            String exampleType,
            String code,
            String language,
            String explanation,
            String highlightLines,
            String source,
            Long feedbackId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new RuleExampleJpaEntity(
                id,
                ruleId,
                exampleType,
                code,
                language,
                explanation,
                highlightLines,
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

    public String getExampleType() {
        return exampleType;
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return language;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getHighlightLines() {
        return highlightLines;
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
        RuleExampleJpaEntity that = (RuleExampleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
