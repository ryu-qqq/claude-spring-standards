package com.ryuqq.adapter.out.persistence.convention.entity;

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
 * ConventionJpaEntity - 컨벤션 정의 JPA 엔티티
 *
 * <p>convention 테이블과 매핑됩니다.
 *
 * <p>ENT-002: JPA 관계 어노테이션 금지 - Long FK 전략 사용.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "convention")
public class ConventionJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "version", length = 20, nullable = false)
    private String version;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    protected ConventionJpaEntity() {}

    private ConventionJpaEntity(
            Long id,
            Long moduleId,
            String version,
            String description,
            boolean isActive,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.moduleId = moduleId;
        this.version = version;
        this.description = description;
        this.isActive = isActive;
    }

    public static ConventionJpaEntity of(
            Long id,
            Long moduleId,
            String version,
            String description,
            boolean isActive,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ConventionJpaEntity(
                id, moduleId, version, description, isActive, createdAt, updatedAt, deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConventionJpaEntity that = (ConventionJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
