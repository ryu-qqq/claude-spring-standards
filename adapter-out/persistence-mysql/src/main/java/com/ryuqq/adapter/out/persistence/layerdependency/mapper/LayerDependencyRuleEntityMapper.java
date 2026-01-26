package com.ryuqq.adapter.out.persistence.layerdependency.mapper;

import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleEntityMapper - LayerDependencyRule Entity <-> Domain 변환
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
public class LayerDependencyRuleEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public LayerDependencyRuleEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return LayerDependencyRule 도메인 객체
     */
    public LayerDependencyRule toDomain(LayerDependencyRuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return LayerDependencyRule.reconstitute(
                LayerDependencyRuleId.of(entity.getId()),
                ArchitectureId.of(entity.getArchitectureId()),
                LayerType.valueOf(entity.getFromLayer()),
                LayerType.valueOf(entity.getToLayer()),
                DependencyType.valueOf(entity.getDependencyType()),
                toConditionDescription(entity.getConditionDescription()),
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
     * @param domain LayerDependencyRule 도메인 객체
     * @return JPA 엔티티
     */
    public LayerDependencyRuleJpaEntity toEntity(LayerDependencyRule domain) {
        if (domain == null) {
            return null;
        }
        return LayerDependencyRuleJpaEntity.of(
                domain.idValue(),
                domain.architectureIdValue(),
                domain.fromLayerName(),
                domain.toLayerName(),
                domain.dependencyTypeName(),
                domain.conditionDescriptionValue(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private ConditionDescription toConditionDescription(String value) {
        if (value == null || value.isBlank()) {
            return ConditionDescription.empty();
        }
        return ConditionDescription.of(value);
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(LayerDependencyRuleJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
