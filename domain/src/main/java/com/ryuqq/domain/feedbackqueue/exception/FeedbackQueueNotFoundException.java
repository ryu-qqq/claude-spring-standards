package com.ryuqq.domain.feedbackqueue.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * FeedbackQueueNotFoundException - 피드백 큐 미존재 예외
 *
 * @author ryu-qqq
 */
public class FeedbackQueueNotFoundException extends DomainException {

    public FeedbackQueueNotFoundException(Long feedbackQueueId) {
        super(
                FeedbackQueueErrorCode.FEEDBACK_QUEUE_NOT_FOUND,
                String.format("FeedbackQueue not found: %d", feedbackQueueId),
                Map.of("feedbackQueueId", feedbackQueueId));
    }
}
