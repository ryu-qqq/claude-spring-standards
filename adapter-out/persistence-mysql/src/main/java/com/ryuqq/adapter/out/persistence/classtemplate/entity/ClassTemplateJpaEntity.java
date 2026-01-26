package com.ryuqq.adapter.out.persistence.classtemplate.entity;

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
 * ClassTemplateJpaEntity - 클래스 템플릿 JPA 엔티티
 *
 * <p>class_template 테이블과 매핑됩니다.
 *
 * <p>PackageStructure의 하위 엔티티입니다 (structureId FK).
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "class_template")
public class ClassTemplateJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "structure_id", nullable = false)
    private Long structureId;

    @Column(name = "class_type_id", nullable = false)
    private Long classTypeId;

    @Column(name = "template_code", columnDefinition = "TEXT", nullable = false)
    private String templateCode;

    @Column(name = "naming_pattern", length = 200)
    private String namingPattern;

    @Column(name = "required_annotations", columnDefinition = "JSON")
    private String requiredAnnotations;

    @Column(name = "forbidden_annotations", columnDefinition = "JSON")
    private String forbiddenAnnotations;

    @Column(name = "required_interfaces", columnDefinition = "JSON")
    private String requiredInterfaces;

    @Column(name = "forbidden_inheritance", columnDefinition = "JSON")
    private String forbiddenInheritance;

    @Column(name = "required_methods", columnDefinition = "JSON")
    private String requiredMethods;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    protected ClassTemplateJpaEntity() {}

    private ClassTemplateJpaEntity(
            Long id,
            Long structureId,
            Long classTypeId,
            String templateCode,
            String namingPattern,
            String requiredAnnotations,
            String forbiddenAnnotations,
            String requiredInterfaces,
            String forbiddenInheritance,
            String requiredMethods,
            String description,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.structureId = structureId;
        this.classTypeId = classTypeId;
        this.templateCode = templateCode;
        this.namingPattern = namingPattern;
        this.requiredAnnotations = requiredAnnotations;
        this.forbiddenAnnotations = forbiddenAnnotations;
        this.requiredInterfaces = requiredInterfaces;
        this.forbiddenInheritance = forbiddenInheritance;
        this.requiredMethods = requiredMethods;
        this.description = description;
    }

    public static ClassTemplateJpaEntity of(
            Long id,
            Long structureId,
            Long classTypeId,
            String templateCode,
            String namingPattern,
            String requiredAnnotations,
            String forbiddenAnnotations,
            String requiredInterfaces,
            String forbiddenInheritance,
            String requiredMethods,
            String description,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ClassTemplateJpaEntity(
                id,
                structureId,
                classTypeId,
                templateCode,
                namingPattern,
                requiredAnnotations,
                forbiddenAnnotations,
                requiredInterfaces,
                forbiddenInheritance,
                requiredMethods,
                description,
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

    public Long getClassTypeId() {
        return classTypeId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getNamingPattern() {
        return namingPattern;
    }

    public String getRequiredAnnotations() {
        return requiredAnnotations;
    }

    public String getForbiddenAnnotations() {
        return forbiddenAnnotations;
    }

    public String getRequiredInterfaces() {
        return requiredInterfaces;
    }

    public String getForbiddenInheritance() {
        return forbiddenInheritance;
    }

    public String getRequiredMethods() {
        return requiredMethods;
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
        ClassTemplateJpaEntity that = (ClassTemplateJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
