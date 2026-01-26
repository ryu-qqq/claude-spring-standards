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

    @Column(name = "default_allowed_class_types", columnDefinition = "JSON")
    private String defaultAllowedClassTypes;

    @Column(name = "default_naming_pattern", length = 200)
    private String defaultNamingPattern;

    @Column(name = "default_naming_suffix", length = 50)
    private String defaultNamingSuffix;

    protected PackagePurposeJpaEntity() {}

    private PackagePurposeJpaEntity(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            String defaultAllowedClassTypes,
            String defaultNamingPattern,
            String defaultNamingSuffix,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.structureId = structureId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.defaultAllowedClassTypes = defaultAllowedClassTypes;
        this.defaultNamingPattern = defaultNamingPattern;
        this.defaultNamingSuffix = defaultNamingSuffix;
    }

    public static PackagePurposeJpaEntity of(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            String defaultAllowedClassTypes,
            String defaultNamingPattern,
            String defaultNamingSuffix,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new PackagePurposeJpaEntity(
                id,
                structureId,
                code,
                name,
                description,
                defaultAllowedClassTypes,
                defaultNamingPattern,
                defaultNamingSuffix,
                createdAt,
                updatedAt,
                deletedAt);
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

    public String getDefaultAllowedClassTypes() {
        return defaultAllowedClassTypes;
    }

    public String getDefaultNamingPattern() {
        return defaultNamingPattern;
    }

    public String getDefaultNamingSuffix() {
        return defaultNamingSuffix;
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
