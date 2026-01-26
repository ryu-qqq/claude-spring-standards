package com.ryuqq.domain.feedbackqueue.aggregate;

import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackStatusTransitionException;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackPayload;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.ReviewNotes;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;

/**
 * FeedbackQueue - 피드백 큐 Aggregate Root
 *
 * <p>MCP 피드백 시스템의 피드백 요청을 관리합니다. 상태 전이는 내부 메서드를 통해서만 수행됩니다.
 *
 * <p><strong>상태 전이 규칙:</strong>
 *
 * <pre>
 * PENDING → LLM_APPROVED (llmApprove)
 * PENDING → LLM_REJECTED (llmReject) [Terminal]
 * LLM_APPROVED + SAFE → MERGED (merge)
 * LLM_APPROVED + MEDIUM → HUMAN_APPROVED (humanApprove)
 * LLM_APPROVED + MEDIUM → HUMAN_REJECTED (humanReject) [Terminal]
 * HUMAN_APPROVED → MERGED (merge) [Terminal]
 * </pre>
 *
 * @author ryu-qqq
 */
public class FeedbackQueue {

    private FeedbackQueueId id;
    private final FeedbackTargetType targetType;
    private final Long targetId;
    private final FeedbackType feedbackType;
    private final FeedbackPayload payload;
    private FeedbackStatus status;
    private final RiskLevel riskLevel;
    private ReviewNotes reviewNotes;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected FeedbackQueue() {
        this.targetType = null;
        this.targetId = null;
        this.feedbackType = null;
        this.payload = null;
        this.riskLevel = null;
        this.createdAt = null;
    }

    private FeedbackQueue(
            FeedbackQueueId id,
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            FeedbackPayload payload,
            FeedbackStatus status,
            RiskLevel riskLevel,
            ReviewNotes reviewNotes,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.feedbackType = feedbackType;
        this.payload = payload;
        this.status = status;
        this.riskLevel = riskLevel;
        this.reviewNotes = reviewNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param targetType 대상 타입
     * @param targetId 대상 ID (MODIFY, DELETE 시 필수)
     * @param feedbackType 피드백 유형
     * @param payload 피드백 페이로드 (JSON)
     * @param now 현재 시각
     * @return 새로운 FeedbackQueue 인스턴스
     */
    public static FeedbackQueue forNew(
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            FeedbackPayload payload,
            Instant now) {
        return forNew(targetType, targetId, feedbackType, payload, targetType.riskLevel(), now);
    }

    /**
     * 신규 생성용 팩토리 메서드 (RiskLevel 명시)
     *
     * <p>입력 시점 검증을 통해 동적으로 RiskLevel이 결정된 경우 사용합니다. 예: 부모 엔티티가 없는 경우 MEDIUM으로 승격
     *
     * @param targetType 대상 타입
     * @param targetId 대상 ID (MODIFY, DELETE 시 필수)
     * @param feedbackType 피드백 유형
     * @param payload 피드백 페이로드 (JSON)
     * @param riskLevel 검증을 통해 결정된 RiskLevel
     * @param now 현재 시각
     * @return 새로운 FeedbackQueue 인스턴스
     */
    public static FeedbackQueue forNew(
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            FeedbackPayload payload,
            RiskLevel riskLevel,
            Instant now) {
        validateTargetId(feedbackType, targetId);
        return new FeedbackQueue(
                FeedbackQueueId.forNew(),
                targetType,
                targetId,
                feedbackType,
                payload,
                FeedbackStatus.PENDING,
                riskLevel,
                ReviewNotes.empty(),
                now,
                now);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 피드백 큐 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @param feedbackType 피드백 유형
     * @param payload 피드백 페이로드
     * @param status 현재 상태
     * @param riskLevel 위험도
     * @param reviewNotes 리뷰 노트
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 FeedbackQueue 인스턴스
     */
    public static FeedbackQueue reconstitute(
            FeedbackQueueId id,
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            FeedbackPayload payload,
            FeedbackStatus status,
            RiskLevel riskLevel,
            ReviewNotes reviewNotes,
            Instant createdAt,
            Instant updatedAt) {
        return new FeedbackQueue(
                id,
                targetType,
                targetId,
                feedbackType,
                payload,
                status,
                riskLevel,
                reviewNotes,
                createdAt,
                updatedAt);
    }

    private static void validateTargetId(FeedbackType feedbackType, Long targetId) {
        if (!feedbackType.isAdd() && targetId == null) {
            throw new IllegalArgumentException(
                    "targetId is required for " + feedbackType + " feedback");
        }
    }

    // === 상태 전이 메서드 (Tell, Don't Ask) ===

    /**
     * LLM 승인 처리
     *
     * <p>PENDING → LLM_APPROVED 상태 전이
     *
     * @param notes 승인 노트 (optional)
     * @param now 현재 시각
     */
    public void llmApprove(ReviewNotes notes, Instant now) {
        validateCanLlmApprove();
        this.status = FeedbackStatus.LLM_APPROVED;
        this.reviewNotes = notes != null ? notes : ReviewNotes.empty();
        this.updatedAt = now;
    }

    /**
     * LLM 거절 처리
     *
     * <p>PENDING → LLM_REJECTED 상태 전이 (Terminal)
     *
     * @param notes 거절 사유 (필수 권장)
     * @param now 현재 시각
     */
    public void llmReject(ReviewNotes notes, Instant now) {
        validateCanLlmReject();
        this.status = FeedbackStatus.LLM_REJECTED;
        this.reviewNotes = notes != null ? notes : ReviewNotes.empty();
        this.updatedAt = now;
    }

    /**
     * 사람 승인 처리
     *
     * <p>LLM_APPROVED → HUMAN_APPROVED 상태 전이 MEDIUM 위험도인 경우에만 유효
     *
     * @param notes 승인 노트 (optional)
     * @param now 현재 시각
     */
    public void humanApprove(ReviewNotes notes, Instant now) {
        validateCanHumanApprove();
        this.status = FeedbackStatus.HUMAN_APPROVED;
        this.reviewNotes = notes != null ? notes : this.reviewNotes;
        this.updatedAt = now;
    }

    /**
     * 사람 거절 처리
     *
     * <p>LLM_APPROVED → HUMAN_REJECTED 상태 전이 (Terminal) MEDIUM 위험도인 경우에만 유효
     *
     * @param notes 거절 사유 (필수 권장)
     * @param now 현재 시각
     */
    public void humanReject(ReviewNotes notes, Instant now) {
        validateCanHumanReject();
        this.status = FeedbackStatus.HUMAN_REJECTED;
        this.reviewNotes = notes != null ? notes : this.reviewNotes;
        this.updatedAt = now;
    }

    /**
     * 병합 처리
     *
     * <p>LLM_APPROVED (SAFE) → MERGED 또는 HUMAN_APPROVED → MERGED 상태 전이 (Terminal)
     *
     * @param now 현재 시각
     */
    public void merge(Instant now) {
        validateCanMerge();
        this.status = FeedbackStatus.MERGED;
        this.updatedAt = now;
    }

    // === 유효성 검증 메서드 ===

    private void validateCanLlmApprove() {
        if (!status.canLlmApprove()) {
            throw new InvalidFeedbackStatusTransitionException(idValue(), status, "llmApprove");
        }
    }

    private void validateCanLlmReject() {
        if (!status.canLlmReject()) {
            throw new InvalidFeedbackStatusTransitionException(idValue(), status, "llmReject");
        }
    }

    private void validateCanHumanApprove() {
        if (!status.canHumanApprove()) {
            throw new InvalidFeedbackStatusTransitionException(idValue(), status, "humanApprove");
        }
        if (riskLevel.isAutoMergeable()) {
            throw new InvalidFeedbackStatusTransitionException(
                    idValue(),
                    status,
                    "humanApprove (SAFE risk level does not require human approval)");
        }
    }

    private void validateCanHumanReject() {
        if (!status.canHumanReject()) {
            throw new InvalidFeedbackStatusTransitionException(idValue(), status, "humanReject");
        }
        if (riskLevel.isAutoMergeable()) {
            throw new InvalidFeedbackStatusTransitionException(
                    idValue(),
                    status,
                    "humanReject (SAFE risk level does not require human review)");
        }
    }

    private void validateCanMerge() {
        if (!status.canMerge()) {
            throw new InvalidFeedbackStatusTransitionException(idValue(), status, "merge");
        }
        // LLM_APPROVED + MEDIUM인 경우 사람 승인이 필요
        if (status == FeedbackStatus.LLM_APPROVED && riskLevel.requiresHumanApproval()) {
            throw new InvalidFeedbackStatusTransitionException(
                    idValue(), status, "merge (MEDIUM risk level requires human approval first)");
        }
    }

    // === 쿼리 메서드 ===

    /**
     * 신규 엔티티 여부 확인
     *
     * @return ID가 null이면 true
     */
    public boolean isNew() {
        return id.isNew();
    }

    /**
     * ID 할당 (영속화 후 호출)
     *
     * @param id 할당할 ID
     */
    public void assignId(FeedbackQueueId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 자동 병합 가능 여부 확인
     *
     * @return LLM_APPROVED 상태이고 SAFE 위험도인 경우 true
     */
    public boolean canAutoMerge() {
        return status == FeedbackStatus.LLM_APPROVED && riskLevel.isAutoMergeable();
    }

    /**
     * 사람의 리뷰가 필요한지 확인
     *
     * @return LLM_APPROVED 상태이고 MEDIUM 위험도인 경우 true
     */
    public boolean requiresHumanReview() {
        return status == FeedbackStatus.LLM_APPROVED && riskLevel.requiresHumanApproval();
    }

    /**
     * 종료 상태인지 확인
     *
     * @return 더 이상 상태 전이가 불가능하면 true
     */
    public boolean isTerminal() {
        return status.isTerminal();
    }

    // === Getters ===

    public FeedbackQueueId id() {
        return id;
    }

    public FeedbackTargetType targetType() {
        return targetType;
    }

    public Long targetId() {
        return targetId;
    }

    public FeedbackType feedbackType() {
        return feedbackType;
    }

    public FeedbackPayload payload() {
        return payload;
    }

    public FeedbackStatus status() {
        return status;
    }

    public RiskLevel riskLevel() {
        return riskLevel;
    }

    public ReviewNotes reviewNotes() {
        return reviewNotes;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // === Value Object 위임 메서드 (Law of Demeter 준수) ===

    /**
     * ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return ID 값
     */
    public Long idValue() {
        return id.value();
    }

    /**
     * Payload 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 페이로드 JSON 문자열
     */
    public String payloadValue() {
        return payload.value();
    }

    /**
     * ReviewNotes 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 리뷰 노트 문자열 (nullable)
     */
    public String reviewNotesValue() {
        return reviewNotes.value();
    }
}
