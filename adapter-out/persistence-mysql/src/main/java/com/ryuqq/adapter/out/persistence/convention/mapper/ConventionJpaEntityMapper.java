package com.ryuqq.adapter.out.persistence.convention.mapper;

import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import org.springframework.stereotype.Component;

/**
 * ConventionJpaEntityMapper - Convention Entity ↔ Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p>C-002: 변환기에서 null 체크 금지 (호출자 책임).
 *
 * @author ryu-qqq
 */
@Component
public class ConventionJpaEntityMapper {

    /**
     * JPA Entity → Domain 변환
     *
     * @param entity JPA 엔티티
     * @return Convention 도메인 객체
     */
    public Convention toDomain(ConventionJpaEntity entity) {
        return Convention.reconstitute(
                ConventionId.of(entity.getId()),
                ModuleId.of(entity.getModuleId()),
                ConventionVersion.of(entity.getVersion()),
                entity.getDescription(),
                entity.isActive(),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain → JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain Convention 도메인 객체
     * @return JPA 엔티티
     */
    public ConventionJpaEntity toEntity(Convention domain) {
        return ConventionJpaEntity.of(
                domain.idValue(),
                domain.moduleIdValue(),
                domain.versionValue(),
                domain.description(),
                domain.isActive(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(ConventionJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
