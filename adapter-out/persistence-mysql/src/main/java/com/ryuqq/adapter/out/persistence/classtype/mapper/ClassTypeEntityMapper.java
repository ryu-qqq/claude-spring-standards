package com.ryuqq.adapter.out.persistence.classtype.mapper;

import com.ryuqq.adapter.out.persistence.classtype.entity.ClassTypeJpaEntity;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtype.vo.ClassTypeName;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import org.springframework.stereotype.Component;

/**
 * ClassTypeEntityMapper - ClassType Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeEntityMapper {

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return ClassType 도메인 객체, 입력이 null이면 null 반환
     */
    public ClassType toDomain(ClassTypeJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ClassType.reconstitute(
                ClassTypeId.of(entity.getId()),
                ClassTypeCategoryId.of(entity.getCategoryId()),
                ClassTypeCode.of(entity.getCode()),
                ClassTypeName.of(entity.getName()),
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
     * @param domain ClassType 도메인 객체
     * @return JPA 엔티티
     */
    public ClassTypeJpaEntity toEntity(ClassType domain) {
        if (domain == null) {
            return null;
        }
        return ClassTypeJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.categoryIdValue(),
                domain.codeValue(),
                domain.nameValue(),
                domain.description(),
                domain.orderIndex(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(ClassTypeJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
