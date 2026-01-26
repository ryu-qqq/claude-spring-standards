package com.ryuqq.adapter.out.persistence.configfiletemplate.entity;

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
 * ConfigFileTemplateJpaEntity - AI 도구 설정 파일 템플릿 JPA 엔티티
 *
 * <p>config_file_template 테이블과 매핑됩니다.
 *
 * <p>Convention Hub init 시 이 템플릿을 기반으로 설정 파일을 생성합니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "config_file_template")
public class ConfigFileTemplateJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tech_stack_id", nullable = false)
    private Long techStackId;

    @Column(name = "architecture_id")
    private Long architectureId;

    @Column(name = "tool_type", length = 50, nullable = false)
    private String toolType;

    @Column(name = "file_path", length = 200, nullable = false)
    private String filePath;

    @Column(name = "file_name", length = 100, nullable = false)
    private String fileName;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "variables", columnDefinition = "JSON")
    private String variables;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    protected ConfigFileTemplateJpaEntity() {}

    private ConfigFileTemplateJpaEntity(
            Long id,
            Long techStackId,
            Long architectureId,
            String toolType,
            String filePath,
            String fileName,
            String content,
            String category,
            String description,
            String variables,
            Integer displayOrder,
            Boolean isRequired,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.techStackId = techStackId;
        this.architectureId = architectureId;
        this.toolType = toolType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.content = content;
        this.category = category;
        this.description = description;
        this.variables = variables;
        this.displayOrder = displayOrder;
        this.isRequired = isRequired;
    }

    public static ConfigFileTemplateJpaEntity of(
            Long id,
            Long techStackId,
            Long architectureId,
            String toolType,
            String filePath,
            String fileName,
            String content,
            String category,
            String description,
            String variables,
            Integer displayOrder,
            Boolean isRequired,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ConfigFileTemplateJpaEntity(
                id,
                techStackId,
                architectureId,
                toolType,
                filePath,
                fileName,
                content,
                category,
                description,
                variables,
                displayOrder,
                isRequired,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getTechStackId() {
        return techStackId;
    }

    public Long getArchitectureId() {
        return architectureId;
    }

    public String getToolType() {
        return toolType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getVariables() {
        return variables;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigFileTemplateJpaEntity that = (ConfigFileTemplateJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
