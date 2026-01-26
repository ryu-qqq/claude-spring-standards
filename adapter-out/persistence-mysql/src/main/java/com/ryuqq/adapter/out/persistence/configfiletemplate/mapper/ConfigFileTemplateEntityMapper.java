package com.ryuqq.adapter.out.persistence.configfiletemplate.mapper;

import com.ryuqq.adapter.out.persistence.configfiletemplate.entity.ConfigFileTemplateJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.vo.DisplayOrder;
import com.ryuqq.domain.configfiletemplate.vo.FileName;
import com.ryuqq.domain.configfiletemplate.vo.FilePath;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.TemplateContent;
import com.ryuqq.domain.configfiletemplate.vo.TemplateDescription;
import com.ryuqq.domain.configfiletemplate.vo.TemplateVariables;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateEntityMapper - ConfigFileTemplate Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateEntityMapper {

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return ConfigFileTemplate 도메인 객체, 입력이 null이면 null 반환
     */
    public ConfigFileTemplate toDomain(ConfigFileTemplateJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ConfigFileTemplate.reconstitute(
                ConfigFileTemplateId.of(entity.getId()),
                TechStackId.of(entity.getTechStackId()),
                parseArchitectureId(entity.getArchitectureId()),
                ToolType.valueOf(entity.getToolType()),
                FilePath.of(entity.getFilePath()),
                FileName.of(entity.getFileName()),
                parseTemplateContent(entity.getContent()),
                parseTemplateCategory(entity.getCategory()),
                parseTemplateDescription(entity.getDescription()),
                parseTemplateVariables(entity.getVariables()),
                parseDisplayOrder(entity.getDisplayOrder()),
                entity.getIsRequired() != null ? entity.getIsRequired() : true,
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain ConfigFileTemplate 도메인 객체
     * @return JPA 엔티티
     */
    public ConfigFileTemplateJpaEntity toEntity(ConfigFileTemplate domain) {
        if (domain == null) {
            return null;
        }
        return ConfigFileTemplateJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.techStackIdValue(),
                domain.architectureIdValue(),
                domain.toolTypeName(),
                domain.filePathValue(),
                domain.fileNameValue(),
                domain.contentValue(),
                domain.categoryName(),
                domain.descriptionValue(),
                domain.variablesValue(),
                domain.displayOrderValue(),
                domain.isRequired(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(ConfigFileTemplateJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }

    private ArchitectureId parseArchitectureId(Long value) {
        return value != null ? ArchitectureId.of(value) : null;
    }

    private TemplateContent parseTemplateContent(String value) {
        if (value == null) {
            return TemplateContent.empty();
        }
        return TemplateContent.of(value);
    }

    private TemplateCategory parseTemplateCategory(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return TemplateCategory.valueOf(value);
    }

    private TemplateDescription parseTemplateDescription(String value) {
        if (value == null || value.isBlank()) {
            return TemplateDescription.empty();
        }
        return TemplateDescription.of(value);
    }

    private TemplateVariables parseTemplateVariables(String value) {
        if (value == null || value.isBlank()) {
            return TemplateVariables.empty();
        }
        return TemplateVariables.of(value);
    }

    private DisplayOrder parseDisplayOrder(Integer value) {
        return DisplayOrder.of(value);
    }
}
