package com.ryuqq.adapter.out.persistence.architecture.entity;

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
 * ArchitectureJpaEntity - 아키텍처 패턴 JPA 엔티티
 *
 * <p>architecture 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "architecture")
public class ArchitectureJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tech_stack_id", nullable = false)
    private Long techStackId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "pattern_type", length = 50, nullable = false)
    private String patternType;

    @Column(name = "pattern_description", columnDefinition = "TEXT")
    private String patternDescription;

    @Column(name = "pattern_principles", columnDefinition = "JSON")
    private String patternPrinciples;

    @Column(name = "reference_links", columnDefinition = "JSON")
    private String referenceLinks;

    protected ArchitectureJpaEntity() {}

    private ArchitectureJpaEntity(
            Long id,
            Long techStackId,
            String name,
            String patternType,
            String patternDescription,
            String patternPrinciples,
            String referenceLinks,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.techStackId = techStackId;
        this.name = name;
        this.patternType = patternType;
        this.patternDescription = patternDescription;
        this.patternPrinciples = patternPrinciples;
        this.referenceLinks = referenceLinks;
    }

    public static ArchitectureJpaEntity of(
            Long id,
            Long techStackId,
            String name,
            String patternType,
            String patternDescription,
            String patternPrinciples,
            String referenceLinks,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ArchitectureJpaEntity(
                id,
                techStackId,
                name,
                patternType,
                patternDescription,
                patternPrinciples,
                referenceLinks,
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

    public String getName() {
        return name;
    }

    public String getPatternType() {
        return patternType;
    }

    public String getPatternDescription() {
        return patternDescription;
    }

    public String getPatternPrinciples() {
        return patternPrinciples;
    }

    public String getReferenceLinks() {
        return referenceLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchitectureJpaEntity that = (ArchitectureJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
