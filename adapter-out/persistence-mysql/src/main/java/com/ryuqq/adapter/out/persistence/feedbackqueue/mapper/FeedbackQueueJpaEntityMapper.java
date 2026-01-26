package com.ryuqq.adapter.out.persistence.feedbackqueue.mapper;

import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackPayload;
import com.ryuqq.domain.feedbackqueue.vo.ReviewNotes;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueJpaEntityMapper - FeedbackQueue Entity <-> Domain 변환
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
public class FeedbackQueueJpaEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public FeedbackQueueJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return FeedbackQueue 도메인 객체
     */
    public FeedbackQueue toDomain(FeedbackQueueJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return FeedbackQueue.reconstitute(
                FeedbackQueueId.of(entity.getId()),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getFeedbackType(),
                FeedbackPayload.of(entity.getPayload()),
                entity.getStatus(),
                entity.getRiskLevel(),
                parseReviewNotes(entity.getReviewNotes()),
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
     * @param domain FeedbackQueue 도메인 객체
     * @return JPA 엔티티
     */
    public FeedbackQueueJpaEntity toEntity(FeedbackQueue domain) {
        if (domain == null) {
            return null;
        }
        return FeedbackQueueJpaEntity.ofInstant(
                domain.idValue(),
                domain.targetType(),
                domain.targetId(),
                domain.feedbackType(),
                domain.riskLevel(),
                domain.payloadValue(),
                domain.status(),
                domain.reviewNotesValue(),
                domain.createdAt(),
                domain.updatedAt());
    }

    /**
     * 문자열 -> ReviewNotes 변환
     *
     * @param reviewNotes 리뷰 노트 문자열
     * @return ReviewNotes 객체
     */
    private ReviewNotes parseReviewNotes(String reviewNotes) {
        if (reviewNotes == null || reviewNotes.isBlank()) {
            return ReviewNotes.empty();
        }
        return ReviewNotes.of(reviewNotes);
    }
}
