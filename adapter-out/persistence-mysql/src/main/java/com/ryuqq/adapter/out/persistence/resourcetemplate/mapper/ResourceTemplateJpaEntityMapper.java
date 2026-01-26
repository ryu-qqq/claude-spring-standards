package com.ryuqq.adapter.out.persistence.resourcetemplate.mapper;

import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateJpaEntityMapper - ResourceTemplate Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p>EMAP-002: Pure Java만 사용 (Lombok/MapStruct 금지)
 *
 * <p>EMAP-003: 시간 필드 생성 금지 (Instant.now() 금지)
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateJpaEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public ResourceTemplateJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return ResourceTemplate 도메인 객체
     */
    public ResourceTemplate toDomain(ResourceTemplateJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ResourceTemplate.reconstitute(
                ResourceTemplateId.of(entity.getId()),
                ModuleId.of(entity.getModuleId()),
                mapTemplateCategory(entity.getCategory()),
                TemplatePath.of(entity.getFilePath()),
                mapFileType(entity.getFileType()),
                entity.getDescription(),
                TemplateContent.of(entity.getTemplateContent()),
                entity.isRequired(),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>EMAP-004: toEntity(Domain) 메서드 필수
     *
     * <p>EMAP-006: Entity.of() 호출
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain ResourceTemplate 도메인 객체
     * @return JPA 엔티티
     */
    public ResourceTemplateJpaEntity toEntity(ResourceTemplate domain) {
        if (domain == null) {
            return null;
        }
        return ResourceTemplateJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.moduleIdValue(),
                domain.categoryName(),
                domain.filePathValue(),
                domain.fileTypeName(),
                domain.description(),
                domain.templateContentValue(),
                domain.required(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * TemplateCategory 문자열 -> Enum 변환
     *
     * @param category 카테고리 문자열
     * @return TemplateCategory enum
     */
    private TemplateCategory mapTemplateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("TemplateCategory must not be null or blank");
        }
        return TemplateCategory.valueOf(category);
    }

    /**
     * FileType 문자열 -> Enum 변환
     *
     * @param fileType 파일 타입 문자열
     * @return FileType enum
     */
    private FileType mapFileType(String fileType) {
        if (fileType == null || fileType.isBlank()) {
            throw new IllegalArgumentException("FileType must not be null or blank");
        }
        return FileType.valueOf(fileType);
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(ResourceTemplateJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
