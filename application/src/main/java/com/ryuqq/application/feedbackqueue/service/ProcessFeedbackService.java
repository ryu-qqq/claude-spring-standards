package com.ryuqq.application.feedbackqueue.service;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.factory.command.FeedbackQueueCommandFactory;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueuePersistenceManager;
import com.ryuqq.application.feedbackqueue.port.in.ProcessFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.validator.FeedbackQueueValidator;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import com.ryuqq.domain.feedbackqueue.vo.ReviewNotes;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * ProcessFeedbackService - 피드백 처리 통합 서비스
 *
 * <p>LLM/Human 승인/거절 4가지 액션을 통합하여 처리합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class ProcessFeedbackService implements ProcessFeedbackUseCase {

    private final FeedbackQueueValidator feedbackQueueValidator;
    private final FeedbackQueuePersistenceManager feedbackQueuePersistenceManager;
    private final FeedbackQueueAssembler feedbackQueueAssembler;
    private final FeedbackQueueCommandFactory feedbackQueueCommandFactory;

    public ProcessFeedbackService(
            FeedbackQueueValidator feedbackQueueValidator,
            FeedbackQueuePersistenceManager feedbackQueuePersistenceManager,
            FeedbackQueueAssembler feedbackQueueAssembler,
            FeedbackQueueCommandFactory feedbackQueueCommandFactory) {
        this.feedbackQueueValidator = feedbackQueueValidator;
        this.feedbackQueuePersistenceManager = feedbackQueuePersistenceManager;
        this.feedbackQueueAssembler = feedbackQueueAssembler;
        this.feedbackQueueCommandFactory = feedbackQueueCommandFactory;
    }

    @Override
    public FeedbackQueueResult execute(ProcessFeedbackCommand command) {
        FeedbackQueue feedbackQueue =
                feedbackQueueValidator.getAndValidateForProcess(
                        command.feedbackId(), command.action());

        applyAction(feedbackQueue, command.action(), command.reviewNotes());

        feedbackQueuePersistenceManager.persist(feedbackQueue);

        return feedbackQueueAssembler.toResult(feedbackQueue);
    }

    private void applyAction(
            FeedbackQueue feedbackQueue, FeedbackAction action, String reviewNotesValue) {
        ReviewNotes reviewNotes = resolveReviewNotes(reviewNotesValue);
        Instant now = feedbackQueueCommandFactory.now();

        switch (action) {
            case LLM_APPROVE -> feedbackQueue.llmApprove(reviewNotes, now);
            case LLM_REJECT -> feedbackQueue.llmReject(reviewNotes, now);
            case HUMAN_APPROVE -> feedbackQueue.humanApprove(reviewNotes, now);
            case HUMAN_REJECT -> feedbackQueue.humanReject(reviewNotes, now);
        }
    }

    private ReviewNotes resolveReviewNotes(String reviewNotesValue) {
        if (reviewNotesValue == null || reviewNotesValue.isBlank()) {
            return ReviewNotes.empty();
        }
        return ReviewNotes.of(reviewNotesValue);
    }
}
