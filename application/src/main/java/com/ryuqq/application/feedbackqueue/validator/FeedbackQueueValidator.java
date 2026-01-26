package com.ryuqq.application.feedbackqueue.validator;

import com.ryuqq.application.feedbackqueue.manager.FeedbackQueueReadManager;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackQueueNotFoundException;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackStatusTransitionException;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueValidator - 피드백 큐 검증기
 *
 * <p>피드백 큐 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueValidator {

    private final FeedbackQueueReadManager feedbackQueueReadManager;

    public FeedbackQueueValidator(FeedbackQueueReadManager feedbackQueueReadManager) {
        this.feedbackQueueReadManager = feedbackQueueReadManager;
    }

    /**
     * 피드백 처리를 위한 조회 및 검증
     *
     * <p>내부에서 조회 → 상태 검증 → 반환을 수행합니다.
     *
     * @param feedbackId 피드백 ID
     * @param action 수행할 액션
     * @return 검증된 피드백 큐
     * @throws FeedbackQueueNotFoundException 피드백이 존재하지 않으면
     * @throws InvalidFeedbackStatusTransitionException 유효하지 않은 상태 전이면
     */
    public FeedbackQueue getAndValidateForProcess(Long feedbackId, FeedbackAction action) {
        FeedbackQueueId id = FeedbackQueueId.of(feedbackId);
        FeedbackQueue feedbackQueue = feedbackQueueReadManager.getById(id);

        switch (action) {
            case LLM_APPROVE -> validateCanLlmApprove(feedbackQueue);
            case LLM_REJECT -> validateCanLlmReject(feedbackQueue);
            case HUMAN_APPROVE -> validateCanHumanApprove(feedbackQueue);
            case HUMAN_REJECT -> validateCanHumanReject(feedbackQueue);
        }

        return feedbackQueue;
    }

    /**
     * 머지를 위한 조회 및 검증
     *
     * <p>내부에서 조회 → 머지 가능 상태 검증 → 반환을 수행합니다.
     *
     * @param feedbackId 피드백 ID
     * @return 검증된 피드백 큐
     * @throws FeedbackQueueNotFoundException 피드백이 존재하지 않으면
     * @throws InvalidFeedbackStatusTransitionException 유효하지 않은 상태 전이면
     */
    public FeedbackQueue getAndValidateForMerge(Long feedbackId) {
        FeedbackQueueId id = new FeedbackQueueId(feedbackId);
        FeedbackQueue feedbackQueue = feedbackQueueReadManager.getById(id);
        validateCanMerge(feedbackQueue);
        return feedbackQueue;
    }

    /**
     * 피드백 큐 존재 여부 검증
     *
     * @param feedbackQueueId 피드백 큐 ID
     * @throws FeedbackQueueNotFoundException 피드백 큐가 존재하지 않으면
     */
    public void validateExists(FeedbackQueueId feedbackQueueId) {
        feedbackQueueReadManager.getById(feedbackQueueId);
    }

    private void validateCanLlmApprove(FeedbackQueue feedbackQueue) {
        if (!feedbackQueue.status().equals(FeedbackStatus.PENDING)) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(), feedbackQueue.status(), "llmApprove");
        }
    }

    private void validateCanLlmReject(FeedbackQueue feedbackQueue) {
        if (!feedbackQueue.status().equals(FeedbackStatus.PENDING)) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(), feedbackQueue.status(), "llmReject");
        }
    }

    private void validateCanHumanApprove(FeedbackQueue feedbackQueue) {
        if (!feedbackQueue.status().equals(FeedbackStatus.LLM_APPROVED)) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(), feedbackQueue.status(), "humanApprove");
        }
        if (!feedbackQueue.riskLevel().equals(RiskLevel.MEDIUM)) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(),
                    feedbackQueue.status(),
                    "humanApprove (SAFE risk level does not require human approval)");
        }
    }

    private void validateCanHumanReject(FeedbackQueue feedbackQueue) {
        if (!feedbackQueue.status().equals(FeedbackStatus.LLM_APPROVED)) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(), feedbackQueue.status(), "humanReject");
        }
        if (!feedbackQueue.riskLevel().equals(RiskLevel.MEDIUM)) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(),
                    feedbackQueue.status(),
                    "humanReject (SAFE risk level does not require human review)");
        }
    }

    /**
     * 머지 가능 상태 검증
     *
     * <p>SAFE 리스크는 LLM_APPROVED에서, MEDIUM 리스크는 HUMAN_APPROVED에서 머지 가능합니다.
     *
     * @param feedbackQueue 피드백 큐
     * @throws InvalidFeedbackStatusTransitionException 유효하지 않은 상태 전이면
     */
    public void validateCanMerge(FeedbackQueue feedbackQueue) {
        FeedbackStatus status = feedbackQueue.status();
        RiskLevel riskLevel = feedbackQueue.riskLevel();

        boolean canMerge =
                (riskLevel.equals(RiskLevel.SAFE) && status.equals(FeedbackStatus.LLM_APPROVED))
                        || (riskLevel.equals(RiskLevel.MEDIUM)
                                && status.equals(FeedbackStatus.HUMAN_APPROVED));

        if (!canMerge) {
            throw new InvalidFeedbackStatusTransitionException(
                    feedbackQueue.idValue(), status, "merge");
        }
    }
}
