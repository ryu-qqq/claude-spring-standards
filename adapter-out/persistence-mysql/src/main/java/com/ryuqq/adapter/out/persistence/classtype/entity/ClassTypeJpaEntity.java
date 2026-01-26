package com.ryuqq.adapter.out.persistence.classtype.entity;

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
 * ClassTypeJpaEntity - ClassType JPA 엔티티
 *
 * <p>class_type 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "class_type")
public class ClassTypeJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    protected ClassTypeJpaEntity() {}

    private ClassTypeJpaEntity(
            Long id,
            Long categoryId,
            String code,
            String name,
            String description,
            int orderIndex,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.categoryId = categoryId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.orderIndex = orderIndex;
    }

    public static ClassTypeJpaEntity of(
            Long id,
            Long categoryId,
            String code,
            String name,
            String description,
            int orderIndex,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ClassTypeJpaEntity(
                id,
                categoryId,
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

    public Long getCategoryId() {
        return categoryId;
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
        ClassTypeJpaEntity that = (ClassTypeJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
