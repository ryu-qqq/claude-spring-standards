package com.ryuqq.adapter.out.persistence.codingrule.mapper;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.SdkConstraint;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CodingRuleJpaEntityMapper - CodingRule Entity ↔ Domain 변환
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
public class CodingRuleJpaEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public CodingRuleJpaEntityMapper() {}

    /**
     * JPA Entity → Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return CodingRule 도메인 객체
     */
    public CodingRule toDomain(CodingRuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return CodingRule.reconstitute(
                CodingRuleId.of(entity.getId()),
                ConventionId.of(entity.getConventionId()),
                null, // structureId - Entity에서 제거됨
                RuleCode.of(entity.getCode()),
                RuleName.of(entity.getName()),
                entity.getSeverity(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getRationale(),
                entity.isAutoFixable(),
                parseAppliesTo(entity.getAppliesTo()),
                SdkConstraint.empty(), // sdkConstraint - Entity에서 제거됨
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain → JPA Entity 변환
     *
     * <p>EMAP-004: toEntity(Domain) 메서드 필수
     *
     * <p>EMAP-006: Entity.of() 호출
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain CodingRule 도메인 객체
     * @return JPA 엔티티
     */
    public CodingRuleJpaEntity toEntity(CodingRule domain) {
        if (domain == null) {
            return null;
        }
        return CodingRuleJpaEntity.ofInstant(
                domain.idValue(),
                domain.conventionIdValue(),
                domain.codeValue(),
                domain.nameValue(),
                domain.severity(),
                domain.category(),
                domain.description(),
                domain.rationale(),
                domain.isAutoFixable(),
                serializeAppliesTo(domain.appliesTo()),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * 쉼표 구분 문자열 → AppliesTo 변환
     *
     * @param commaSeparated 쉼표로 구분된 문자열
     * @return AppliesTo 객체
     */
    private AppliesTo parseAppliesTo(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isBlank()) {
            return AppliesTo.empty();
        }
        List<String> targets = Arrays.asList(commaSeparated.split(","));
        return AppliesTo.of(targets);
    }

    /**
     * AppliesTo → 쉼표 구분 문자열 변환
     *
     * @param appliesTo AppliesTo 객체
     * @return 쉼표로 구분된 문자열
     */
    private String serializeAppliesTo(AppliesTo appliesTo) {
        if (appliesTo == null || appliesTo.isEmpty()) {
            return null;
        }
        return String.join(",", appliesTo.targets());
    }

    /**
     * Entity의 삭제 상태 → DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(CodingRuleJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
