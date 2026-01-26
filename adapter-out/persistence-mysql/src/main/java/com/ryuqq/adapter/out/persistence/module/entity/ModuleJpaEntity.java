package com.ryuqq.adapter.out.persistence.module.entity;

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
 * ModuleJpaEntity - 모듈 정의 JPA 엔티티
 *
 * <p>module 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "module")
public class ModuleJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "layer_id", nullable = false)
    private Long layerId;

    @Column(name = "parent_module_id")
    private Long parentModuleId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "module_path", length = 500, nullable = false)
    private String modulePath;

    @Column(name = "build_identifier", length = 200)
    private String buildIdentifier;

    protected ModuleJpaEntity() {}

    private ModuleJpaEntity(
            Long id,
            Long layerId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.layerId = layerId;
        this.parentModuleId = parentModuleId;
        this.name = name;
        this.description = description;
        this.modulePath = modulePath;
        this.buildIdentifier = buildIdentifier;
    }

    public static ModuleJpaEntity of(
            Long id,
            Long layerId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ModuleJpaEntity(
                id,
                layerId,
                parentModuleId,
                name,
                description,
                modulePath,
                buildIdentifier,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getLayerId() {
        return layerId;
    }

    public Long getParentModuleId() {
        return parentModuleId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModulePath() {
        return modulePath;
    }

    public String getBuildIdentifier() {
        return buildIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModuleJpaEntity that = (ModuleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
