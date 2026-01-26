package com.ryuqq.adapter.out.persistence.classtypecategory.mapper;

import com.ryuqq.adapter.out.persistence.classtypecategory.entity.ClassTypeCategoryJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import com.ryuqq.domain.classtypecategory.vo.CategoryName;
import com.ryuqq.domain.common.vo.DeletionStatus;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryEntityMapper - ClassTypeCategory Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryEntityMapper {

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return ClassTypeCategory 도메인 객체, 입력이 null이면 null 반환
     */
    public ClassTypeCategory toDomain(ClassTypeCategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ClassTypeCategory.reconstitute(
                ClassTypeCategoryId.of(entity.getId()),
                ArchitectureId.of(entity.getArchitectureId()),
                CategoryCode.of(entity.getCode()),
                CategoryName.of(entity.getName()),
                entity.getDescription(),
                entity.getOrderIndex(),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain ClassTypeCategory 도메인 객체
     * @return JPA 엔티티
     */
    public ClassTypeCategoryJpaEntity toEntity(ClassTypeCategory domain) {
        if (domain == null) {
            return null;
        }
        return ClassTypeCategoryJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.architectureIdValue(),
                domain.codeValue(),
                domain.nameValue(),
                domain.description(),
                domain.orderIndex(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(ClassTypeCategoryJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
