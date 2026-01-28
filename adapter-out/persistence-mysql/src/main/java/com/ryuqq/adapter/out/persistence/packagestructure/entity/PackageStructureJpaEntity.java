package com.ryuqq.adapter.out.persistence.packagestructure.entity;

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
 * PackageStructureJpaEntity - 패키지 구조 정의 JPA 엔티티
 *
 * <p>package_structure 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "package_structure")
public class PackageStructureJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "path_pattern", length = 300, nullable = false)
    private String pathPattern;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    protected PackageStructureJpaEntity() {}

    private PackageStructureJpaEntity(
            Long id,
            Long moduleId,
            String pathPattern,
            String description,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.moduleId = moduleId;
        this.pathPattern = pathPattern;
        this.description = description;
    }

    public static PackageStructureJpaEntity of(
            Long id,
            Long moduleId,
            String pathPattern,
            String description,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new PackageStructureJpaEntity(
                id, moduleId, pathPattern, description, createdAt, updatedAt, deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getPathPattern() {
        return pathPattern;
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
        PackageStructureJpaEntity that = (PackageStructureJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
