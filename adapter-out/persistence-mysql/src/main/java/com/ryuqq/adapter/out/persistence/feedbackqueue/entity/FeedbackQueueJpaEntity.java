package com.ryuqq.adapter.out.persistence.feedbackqueue.entity;

import com.ryuqq.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * FeedbackQueueJpaEntity - 피드백 큐 JPA 엔티티
 *
 * <p>feedback_queue 테이블과 매핑됩니다.
 *
 * <p>Long FK 전략을 사용하여 JPA 관계 어노테이션을 사용하지 않습니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "feedback_queue")
public class FeedbackQueueJpaEntity extends BaseAuditEntity {

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 50, nullable = false)
    private FeedbackTargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", length = 20, nullable = false)
    private FeedbackType feedbackType;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20, nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private FeedbackStatus status;

    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;

    protected FeedbackQueueJpaEntity() {}

    private FeedbackQueueJpaEntity(
            Long id,
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            RiskLevel riskLevel,
            String payload,
            FeedbackStatus status,
            String reviewNotes,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.feedbackType = feedbackType;
        this.riskLevel = riskLevel;
        this.payload = payload;
        this.status = status;
        this.reviewNotes = reviewNotes;
    }

    /**
     * 정적 팩토리 메서드 (LocalDateTime 기반 - 테스트 호환용)
     *
     * @param id 피드백 큐 ID
     * @param targetType 피드백 대상 타입
     * @param targetId 피드백 대상 ID
     * @param feedbackType 피드백 유형
     * @param riskLevel 리스크 수준
     * @param payload 피드백 내용 (JSON)
     * @param status 처리 상태
     * @param reviewNotes 검토 노트
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return FeedbackQueueJpaEntity 인스턴스
     */
    public static FeedbackQueueJpaEntity of(
            Long id,
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            RiskLevel riskLevel,
            String payload,
            FeedbackStatus status,
            String reviewNotes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new FeedbackQueueJpaEntity(
                id,
                targetType,
                targetId,
                feedbackType,
                riskLevel,
                payload,
                status,
                reviewNotes,
                toInstant(createdAt),
                toInstant(updatedAt));
    }

    /**
     * 정적 팩토리 메서드 (Instant 기반 - Mapper 사용)
     *
     * @param id 피드백 큐 ID
     * @param targetType 피드백 대상 타입
     * @param targetId 피드백 대상 ID
     * @param feedbackType 피드백 유형
     * @param riskLevel 리스크 수준
     * @param payload 피드백 내용 (JSON)
     * @param status 처리 상태
     * @param reviewNotes 검토 노트
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return FeedbackQueueJpaEntity 인스턴스
     */
    public static FeedbackQueueJpaEntity ofInstant(
            Long id,
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            RiskLevel riskLevel,
            String payload,
            FeedbackStatus status,
            String reviewNotes,
            Instant createdAt,
            Instant updatedAt) {
        return new FeedbackQueueJpaEntity(
                id,
                targetType,
                targetId,
                feedbackType,
                riskLevel,
                payload,
                status,
                reviewNotes,
                createdAt,
                updatedAt);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(SYSTEM_ZONE).toInstant();
    }

    public Long getId() {
        return id;
    }

    public FeedbackTargetType getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getPayload() {
        return payload;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeedbackQueueJpaEntity that = (FeedbackQueueJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
