package com.ryuqq.adapter.out.persistence.onboardingcontext.entity;

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
 * OnboardingContextJpaEntity - Serena 온보딩 컨텍스트 JPA 엔티티
 *
 * <p>onboarding_context 테이블과 매핑됩니다.
 *
 * <p>Serena onboarding 시 컨벤션 요약 정보를 제공합니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "onboarding_context")
public class OnboardingContextJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tech_stack_id", nullable = false)
    private Long techStackId;

    @Column(name = "architecture_id")
    private Long architectureId;

    @Column(name = "context_type", length = 50, nullable = false)
    private String contextType;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "priority")
    private Integer priority;

    protected OnboardingContextJpaEntity() {}

    private OnboardingContextJpaEntity(
            Long id,
            Long techStackId,
            Long architectureId,
            String contextType,
            String title,
            String content,
            Integer priority,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.techStackId = techStackId;
        this.architectureId = architectureId;
        this.contextType = contextType;
        this.title = title;
        this.content = content;
        this.priority = priority;
    }

    public static OnboardingContextJpaEntity of(
            Long id,
            Long techStackId,
            Long architectureId,
            String contextType,
            String title,
            String content,
            Integer priority,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new OnboardingContextJpaEntity(
                id,
                techStackId,
                architectureId,
                contextType,
                title,
                content,
                priority,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getTechStackId() {
        return techStackId;
    }

    public Long getArchitectureId() {
        return architectureId;
    }

    public String getContextType() {
        return contextType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OnboardingContextJpaEntity that = (OnboardingContextJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
