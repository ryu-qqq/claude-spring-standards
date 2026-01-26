package com.ryuqq.adapter.out.persistence.layerdependency.entity;

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
 * LayerDependencyRuleJpaEntity - 레이어 의존성 규칙 JPA 엔티티
 *
 * <p>layer_dependency_rule 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "layer_dependency_rule")
public class LayerDependencyRuleJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "architecture_id", nullable = false)
    private Long architectureId;

    @Column(name = "from_layer", length = 50, nullable = false)
    private String fromLayer;

    @Column(name = "to_layer", length = 50, nullable = false)
    private String toLayer;

    @Column(name = "dependency_type", length = 30, nullable = false)
    private String dependencyType;

    @Column(name = "condition_description", columnDefinition = "TEXT")
    private String conditionDescription;

    protected LayerDependencyRuleJpaEntity() {}

    private LayerDependencyRuleJpaEntity(
            Long id,
            Long architectureId,
            String fromLayer,
            String toLayer,
            String dependencyType,
            String conditionDescription,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.architectureId = architectureId;
        this.fromLayer = fromLayer;
        this.toLayer = toLayer;
        this.dependencyType = dependencyType;
        this.conditionDescription = conditionDescription;
    }

    public static LayerDependencyRuleJpaEntity of(
            Long id,
            Long architectureId,
            String fromLayer,
            String toLayer,
            String dependencyType,
            String conditionDescription,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new LayerDependencyRuleJpaEntity(
                id,
                architectureId,
                fromLayer,
                toLayer,
                dependencyType,
                conditionDescription,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getArchitectureId() {
        return architectureId;
    }

    public String getFromLayer() {
        return fromLayer;
    }

    public String getToLayer() {
        return toLayer;
    }

    public String getDependencyType() {
        return dependencyType;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LayerDependencyRuleJpaEntity that = (LayerDependencyRuleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
