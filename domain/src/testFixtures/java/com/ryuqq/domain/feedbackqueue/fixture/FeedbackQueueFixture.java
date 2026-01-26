package com.ryuqq.domain.feedbackqueue.fixture;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackPayload;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.ReviewNotes;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;

/**
 * FeedbackQueueFixture - FeedbackQueue 테스트 픽스처
 *
 * @author ryu-qqq
 */
public final class FeedbackQueueFixture {

    private static final Instant DEFAULT_NOW = Instant.parse("2025-01-20T10:00:00Z");

    private FeedbackQueueFixture() {}

    /** PENDING 상태의 SAFE 위험도 피드백 큐 생성 (ADD) */
    public static FeedbackQueue pendingSafeFeedback() {
        return FeedbackQueue.forNew(
                FeedbackTargetType.RULE_EXAMPLE,
                null,
                FeedbackType.ADD,
                FeedbackPayload.of("{\"code\": \"example\"}"),
                DEFAULT_NOW);
    }

    /** PENDING 상태의 MEDIUM 위험도 피드백 큐 생성 (ADD) */
    public static FeedbackQueue pendingMediumFeedback() {
        return FeedbackQueue.forNew(
                FeedbackTargetType.CODING_RULE,
                null,
                FeedbackType.ADD,
                FeedbackPayload.of("{\"rule\": \"new-rule\"}"),
                DEFAULT_NOW);
    }

    /** PENDING 상태의 MODIFY 피드백 큐 생성 */
    public static FeedbackQueue pendingModifyFeedback(Long targetId) {
        return FeedbackQueue.forNew(
                FeedbackTargetType.CLASS_TEMPLATE,
                targetId,
                FeedbackType.MODIFY,
                FeedbackPayload.of("{\"template\": \"updated\"}"),
                DEFAULT_NOW);
    }

    /** PENDING 상태의 DELETE 피드백 큐 생성 */
    public static FeedbackQueue pendingDeleteFeedback(Long targetId) {
        return FeedbackQueue.forNew(
                FeedbackTargetType.CHECKLIST_ITEM,
                targetId,
                FeedbackType.DELETE,
                FeedbackPayload.of("{\"reason\": \"obsolete\"}"),
                DEFAULT_NOW);
    }

    /** LLM_APPROVED 상태의 SAFE 위험도 피드백 큐 생성 */
    public static FeedbackQueue llmApprovedSafeFeedback() {
        FeedbackQueue feedback = pendingSafeFeedback();
        feedback.assignId(FeedbackQueueId.of(1L));
        feedback.llmApprove(ReviewNotes.of("Approved by LLM"), DEFAULT_NOW.plusSeconds(60));
        return feedback;
    }

    /** LLM_APPROVED 상태의 MEDIUM 위험도 피드백 큐 생성 */
    public static FeedbackQueue llmApprovedMediumFeedback() {
        FeedbackQueue feedback = pendingMediumFeedback();
        feedback.assignId(FeedbackQueueId.of(2L));
        feedback.llmApprove(ReviewNotes.of("Approved by LLM"), DEFAULT_NOW.plusSeconds(60));
        return feedback;
    }

    /** LLM_REJECTED 상태의 피드백 큐 생성 */
    public static FeedbackQueue llmRejectedFeedback() {
        FeedbackQueue feedback = pendingSafeFeedback();
        feedback.assignId(FeedbackQueueId.of(3L));
        feedback.llmReject(ReviewNotes.of("Rejected: Invalid format"), DEFAULT_NOW.plusSeconds(60));
        return feedback;
    }

    /** HUMAN_APPROVED 상태의 피드백 큐 생성 */
    public static FeedbackQueue humanApprovedFeedback() {
        FeedbackQueue feedback = llmApprovedMediumFeedback();
        feedback.humanApprove(
                ReviewNotes.of("Approved by human reviewer"), DEFAULT_NOW.plusSeconds(120));
        return feedback;
    }

    /** HUMAN_REJECTED 상태의 피드백 큐 생성 */
    public static FeedbackQueue humanRejectedFeedback() {
        FeedbackQueue feedback = llmApprovedMediumFeedback();
        feedback.humanReject(
                ReviewNotes.of("Rejected: Security concern"), DEFAULT_NOW.plusSeconds(120));
        return feedback;
    }

    /** MERGED 상태의 피드백 큐 생성 (SAFE - 자동 병합) */
    public static FeedbackQueue mergedSafeFeedback() {
        FeedbackQueue feedback = llmApprovedSafeFeedback();
        feedback.merge(DEFAULT_NOW.plusSeconds(120));
        return feedback;
    }

    /** MERGED 상태의 피드백 큐 생성 (MEDIUM - 사람 승인 후 병합) */
    public static FeedbackQueue mergedMediumFeedback() {
        FeedbackQueue feedback = humanApprovedFeedback();
        feedback.merge(DEFAULT_NOW.plusSeconds(180));
        return feedback;
    }

    /** reconstitute용 피드백 큐 생성 */
    public static FeedbackQueue reconstitutedFeedback(
            Long id,
            FeedbackTargetType targetType,
            Long targetId,
            FeedbackType feedbackType,
            FeedbackStatus status,
            RiskLevel riskLevel) {
        return FeedbackQueue.reconstitute(
                FeedbackQueueId.of(id),
                targetType,
                targetId,
                feedbackType,
                FeedbackPayload.of("{\"data\": \"test\"}"),
                status,
                riskLevel,
                ReviewNotes.empty(),
                DEFAULT_NOW,
                DEFAULT_NOW);
    }

    /** 기본 테스트용 NOW 반환 */
    public static Instant defaultNow() {
        return DEFAULT_NOW;
    }
}
