package com.ryuqq.adapter.out.persistence.zerotolerance.mapper;

import com.ryuqq.adapter.out.persistence.zerotolerance.entity.ZeroToleranceRuleJpaEntity;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionPattern;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ErrorMessage;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceType;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleJpaEntityMapper - Zero-Tolerance 규칙 Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p>EMAP-002: Pure Java만 사용 (Lombok/MapStruct 금지)
 *
 * <p>EMAP-003: 시간 필드 생성 금지 (Instant.now() 금지)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleJpaEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public ZeroToleranceRuleJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return ZeroToleranceRule 도메인 객체
     */
    public ZeroToleranceRule toDomain(ZeroToleranceRuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ZeroToleranceRule.reconstitute(
                ZeroToleranceRuleId.of(entity.getId()),
                CodingRuleId.of(entity.getRuleId()),
                ZeroToleranceType.of(entity.getType()),
                DetectionPattern.of(entity.getDetectionPattern()),
                DetectionType.of(entity.getDetectionType()),
                entity.isAutoRejectPr(),
                ErrorMessage.of(entity.getErrorMessage()),
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
     * @param domain ZeroToleranceRule 도메인 객체
     * @return JPA 엔티티
     */
    public ZeroToleranceRuleJpaEntity toEntity(ZeroToleranceRule domain) {
        if (domain == null) {
            return null;
        }
        return ZeroToleranceRuleJpaEntity.of(
                domain.isNew() ? null : domain.id().value(),
                domain.ruleId().value(),
                domain.type().value(),
                domain.detectionPattern().value(),
                domain.detectionType().name(),
                domain.autoRejectPr(),
                domain.errorMessage().value(),
                domain.createdAt(),
                domain.updatedAt(),
                extractDeletedAt(domain));
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(ZeroToleranceRuleJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }

    /**
     * Domain에서 deletedAt 추출
     *
     * @param domain ZeroToleranceRule 도메인 객체
     * @return deletedAt 또는 null
     */
    private java.time.Instant extractDeletedAt(ZeroToleranceRule domain) {
        if (domain.deletionStatus() != null && domain.deletionStatus().isDeleted()) {
            return domain.deletionStatus().deletedAt();
        }
        return null;
    }
}
