package com.ryuqq.adapter.out.persistence.module.mapper;

import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;
import org.springframework.stereotype.Component;

/**
 * ModuleJpaEntityMapper - Module Entity ↔ Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ModuleJpaEntityMapper {

    /**
     * JPA Entity → Domain 변환
     *
     * @param entity JPA 엔티티
     * @return Module 도메인 객체
     */
    public Module toDomain(ModuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Module.reconstitute(
                ModuleId.of(entity.getId()),
                LayerId.of(entity.getLayerId()),
                entity.getParentModuleId() != null ? ModuleId.of(entity.getParentModuleId()) : null,
                ModuleName.of(entity.getName()),
                ModuleDescription.of(entity.getDescription()),
                ModulePath.of(entity.getModulePath()),
                parseBuildIdentifier(entity.getBuildIdentifier()),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain → JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain Module 도메인 객체
     * @return JPA 엔티티
     */
    public ModuleJpaEntity toEntity(Module domain) {
        if (domain == null) {
            return null;
        }
        return ModuleJpaEntity.of(
                domain.idValue(),
                domain.layerIdValue(),
                domain.parentModuleIdValue(),
                domain.nameValue(),
                domain.descriptionValue(),
                domain.modulePathValue(),
                domain.buildIdentifierValue(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(ModuleJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }

    private BuildIdentifier parseBuildIdentifier(String value) {
        if (value == null || value.isBlank()) {
            return BuildIdentifier.empty();
        }
        return BuildIdentifier.of(value);
    }
}
