package com.ryuqq.adapter.out.persistence.resourcetemplate.entity;

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
 * ResourceTemplateJpaEntity - 리소스 템플릿 JPA 엔티티
 *
 * <p>resource_template 테이블과 매핑됩니다.
 *
 * <p>Long FK 전략: moduleId는 Long 타입으로 관리합니다 (JPA 관계 어노테이션 금지).
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "resource_template")
public class ResourceTemplateJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;

    @Column(name = "file_type", length = 20, nullable = false)
    private String fileType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "template_content", columnDefinition = "TEXT")
    private String templateContent;

    @Column(name = "required", nullable = false)
    private boolean required;

    protected ResourceTemplateJpaEntity() {}

    private ResourceTemplateJpaEntity(
            Long id,
            Long moduleId,
            String category,
            String filePath,
            String fileType,
            String description,
            String templateContent,
            boolean required,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.moduleId = moduleId;
        this.category = category;
        this.filePath = filePath;
        this.fileType = fileType;
        this.description = description;
        this.templateContent = templateContent;
        this.required = required;
    }

    public static ResourceTemplateJpaEntity of(
            Long id,
            Long moduleId,
            String category,
            String filePath,
            String fileType,
            String description,
            String templateContent,
            boolean required,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ResourceTemplateJpaEntity(
                id,
                moduleId,
                category,
                filePath,
                fileType,
                description,
                templateContent,
                required,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getCategory() {
        return category;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public String getDescription() {
        return description;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceTemplateJpaEntity that = (ResourceTemplateJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
