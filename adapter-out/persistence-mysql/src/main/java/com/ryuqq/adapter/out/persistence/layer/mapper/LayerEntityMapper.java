package com.ryuqq.adapter.out.persistence.layer.mapper;

import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;
import org.springframework.stereotype.Component;

/**
 * LayerEntityMapper - Layer Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class LayerEntityMapper {

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return Layer 도메인 객체, 입력이 null이면 null 반환
     */
    public Layer toDomain(LayerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Layer.reconstitute(
                LayerId.of(entity.getId()),
                ArchitectureId.of(entity.getArchitectureId()),
                LayerCode.of(entity.getCode()),
                LayerName.of(entity.getName()),
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
     * @param domain Layer 도메인 객체
     * @return JPA 엔티티
     */
    public LayerJpaEntity toEntity(Layer domain) {
        if (domain == null) {
            return null;
        }
        return LayerJpaEntity.of(
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

    private DeletionStatus mapDeletionStatus(LayerJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
