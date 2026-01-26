package com.ryuqq.adapter.out.persistence.classtypecategory.entity;

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
 * ClassTypeCategoryJpaEntity - ClassType 카테고리 JPA 엔티티
 *
 * <p>class_type_category 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "class_type_category")
public class ClassTypeCategoryJpaEntity extends SoftDeletableEntity {

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

    protected ClassTypeCategoryJpaEntity() {}

    private ClassTypeCategoryJpaEntity(
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

    public static ClassTypeCategoryJpaEntity of(
            Long id,
            Long architectureId,
            String code,
            String name,
            String description,
            int orderIndex,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ClassTypeCategoryJpaEntity(
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
        ClassTypeCategoryJpaEntity that = (ClassTypeCategoryJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
