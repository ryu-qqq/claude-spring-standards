package com.ryuqq.application.feedbackqueue.fixture;

import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import java.time.Instant;

/**
 * FeedbackQueueResult Test Fixture
 *
 * @author development-team
 */
public final class FeedbackQueueResultFixture {

    private static final Instant DEFAULT_NOW = Instant.parse("2025-01-20T10:00:00Z");

    private FeedbackQueueResultFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 기본 FeedbackQueueResult 생성 (PENDING 상태)
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult defaultResult() {
        return pendingResult();
    }

    /**
     * PENDING 상태 FeedbackQueueResult 생성
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult pendingResult() {
        return new FeedbackQueueResult(
                1L,
                "RULE_EXAMPLE",
                null,
                "ADD",
                "SAFE",
                "{\"code\": \"example\"}",
                "PENDING",
                null,
                DEFAULT_NOW,
                DEFAULT_NOW);
    }

    /**
     * LLM_APPROVED 상태 FeedbackQueueResult 생성
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult llmApprovedResult() {
        return new FeedbackQueueResult(
                2L,
                "RULE_EXAMPLE",
                null,
                "ADD",
                "SAFE",
                "{\"code\": \"example\"}",
                "LLM_APPROVED",
                "Approved by LLM",
                DEFAULT_NOW,
                DEFAULT_NOW.plusSeconds(60));
    }

    /**
     * LLM_REJECTED 상태 FeedbackQueueResult 생성
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult llmRejectedResult() {
        return new FeedbackQueueResult(
                3L,
                "RULE_EXAMPLE",
                null,
                "ADD",
                "SAFE",
                "{\"code\": \"example\"}",
                "LLM_REJECTED",
                "Rejected: Invalid format",
                DEFAULT_NOW,
                DEFAULT_NOW.plusSeconds(60));
    }

    /**
     * HUMAN_APPROVED 상태 FeedbackQueueResult 생성
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult humanApprovedResult() {
        return new FeedbackQueueResult(
                4L,
                "CODING_RULE",
                null,
                "ADD",
                "MEDIUM",
                "{\"rule\": \"new-rule\"}",
                "HUMAN_APPROVED",
                "Approved by human reviewer",
                DEFAULT_NOW,
                DEFAULT_NOW.plusSeconds(120));
    }

    /**
     * HUMAN_REJECTED 상태 FeedbackQueueResult 생성
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult humanRejectedResult() {
        return new FeedbackQueueResult(
                5L,
                "CODING_RULE",
                null,
                "ADD",
                "MEDIUM",
                "{\"rule\": \"new-rule\"}",
                "HUMAN_REJECTED",
                "Rejected: Security concern",
                DEFAULT_NOW,
                DEFAULT_NOW.plusSeconds(120));
    }

    /**
     * MERGED 상태 FeedbackQueueResult 생성
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult mergedResult() {
        return new FeedbackQueueResult(
                6L,
                "RULE_EXAMPLE",
                null,
                "ADD",
                "SAFE",
                "{\"code\": \"example\"}",
                "MERGED",
                "Approved by LLM",
                DEFAULT_NOW,
                DEFAULT_NOW.plusSeconds(180));
    }

    /**
     * MODIFY 타입 FeedbackQueueResult 생성
     *
     * @param targetId 대상 ID
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult modifyResult(Long targetId) {
        return new FeedbackQueueResult(
                7L,
                "CLASS_TEMPLATE",
                targetId,
                "MODIFY",
                "MEDIUM",
                "{\"template\": \"updated\"}",
                "PENDING",
                null,
                DEFAULT_NOW,
                DEFAULT_NOW);
    }

    /**
     * DELETE 타입 FeedbackQueueResult 생성
     *
     * @param targetId 대상 ID
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult deleteResult(Long targetId) {
        return new FeedbackQueueResult(
                8L,
                "CHECKLIST_ITEM",
                targetId,
                "DELETE",
                "DANGEROUS",
                "{\"reason\": \"obsolete\"}",
                "PENDING",
                null,
                DEFAULT_NOW,
                DEFAULT_NOW);
    }

    /**
     * 도메인 FeedbackQueue로부터 Result 생성
     *
     * @return FeedbackQueueResult (PENDING 상태)
     */
    public static FeedbackQueueResult fromPendingSafeFeedback() {
        return FeedbackQueueResult.from(FeedbackQueueFixture.pendingSafeFeedback());
    }

    /**
     * 도메인 FeedbackQueue로부터 Result 생성 (LLM_APPROVED)
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult fromLlmApprovedFeedback() {
        return FeedbackQueueResult.from(FeedbackQueueFixture.llmApprovedSafeFeedback());
    }

    /**
     * 도메인 FeedbackQueue로부터 Result 생성 (MERGED)
     *
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult fromMergedFeedback() {
        return FeedbackQueueResult.from(FeedbackQueueFixture.mergedSafeFeedback());
    }

    /**
     * 사용자 정의 파라미터로 Result 생성
     *
     * @param id ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID (nullable)
     * @param feedbackType 피드백 타입
     * @param riskLevel 위험 레벨
     * @param payload JSON payload
     * @param status 상태
     * @param reviewNotes 리뷰 노트 (nullable)
     * @return FeedbackQueueResult
     */
    public static FeedbackQueueResult withParams(
            Long id,
            String targetType,
            Long targetId,
            String feedbackType,
            String riskLevel,
            String payload,
            String status,
            String reviewNotes) {
        return new FeedbackQueueResult(
                id,
                targetType,
                targetId,
                feedbackType,
                riskLevel,
                payload,
                status,
                reviewNotes,
                DEFAULT_NOW,
                DEFAULT_NOW);
    }
}
