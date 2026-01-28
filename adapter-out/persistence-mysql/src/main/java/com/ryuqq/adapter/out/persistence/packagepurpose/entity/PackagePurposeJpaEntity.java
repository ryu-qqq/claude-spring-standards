package com.ryuqq.adapter.out.persistence.packagepurpose.entity;

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
 * PackagePurposeJpaEntity - 패키지 목적 정의 JPA 엔티티
 *
 * <p>package_purpose 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "package_purpose")
public class PackagePurposeJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "structure_id", nullable = false)
    private Long structureId;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    protected PackagePurposeJpaEntity() {}

    private PackagePurposeJpaEntity(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.structureId = structureId;
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static PackagePurposeJpaEntity of(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new PackagePurposeJpaEntity(
                id, structureId, code, name, description, createdAt, updatedAt, deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getStructureId() {
        return structureId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PackagePurposeJpaEntity that = (PackagePurposeJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
