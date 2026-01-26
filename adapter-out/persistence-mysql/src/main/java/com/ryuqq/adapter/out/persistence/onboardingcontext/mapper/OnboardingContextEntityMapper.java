package com.ryuqq.adapter.out.persistence.onboardingcontext.mapper;

import com.ryuqq.adapter.out.persistence.onboardingcontext.entity.OnboardingContextJpaEntity;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.vo.ContextContent;
import com.ryuqq.domain.onboardingcontext.vo.ContextTitle;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.onboardingcontext.vo.Priority;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextEntityMapper - OnboardingContext Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextEntityMapper {

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return OnboardingContext 도메인 객체, 입력이 null이면 null 반환
     */
    public OnboardingContext toDomain(OnboardingContextJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(entity.getId()),
                TechStackId.of(entity.getTechStackId()),
                parseArchitectureId(entity.getArchitectureId()),
                ContextType.valueOf(entity.getContextType()),
                ContextTitle.of(entity.getTitle()),
                parseContextContent(entity.getContent()),
                parsePriority(entity.getPriority()),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain OnboardingContext 도메인 객체
     * @return JPA 엔티티
     */
    public OnboardingContextJpaEntity toEntity(OnboardingContext domain) {
        if (domain == null) {
            return null;
        }
        return OnboardingContextJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.techStackIdValue(),
                domain.architectureIdValue(),
                domain.contextTypeName(),
                domain.titleValue(),
                domain.contentValue(),
                domain.priorityValue(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(OnboardingContextJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }

    private ArchitectureId parseArchitectureId(Long value) {
        return value != null ? ArchitectureId.of(value) : null;
    }

    private ContextContent parseContextContent(String value) {
        if (value == null) {
            return ContextContent.empty();
        }
        return ContextContent.of(value);
    }

    private Priority parsePriority(Integer value) {
        return Priority.of(value);
    }
}
