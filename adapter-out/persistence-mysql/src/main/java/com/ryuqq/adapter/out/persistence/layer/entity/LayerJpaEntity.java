package com.ryuqq.adapter.out.persistence.layer.entity;

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
 * LayerJpaEntity - 레이어 JPA 엔티티
 *
 * <p>layer 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "layer")
public class LayerJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "architecture_id", nullable = false)
    private Long architectureId;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    protected LayerJpaEntity() {}

    private LayerJpaEntity(
            Long id,
            Long architectureId,
            String code,
            String name,
            String description,
            int orderIndex,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.architectureId = architectureId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.orderIndex = orderIndex;
    }

    public static LayerJpaEntity of(
            Long id,
            Long architectureId,
            String code,
            String name,
            String description,
            int orderIndex,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new LayerJpaEntity(
                id,
                architectureId,
                code,
                name,
                description,
                orderIndex,
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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LayerJpaEntity that = (LayerJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
