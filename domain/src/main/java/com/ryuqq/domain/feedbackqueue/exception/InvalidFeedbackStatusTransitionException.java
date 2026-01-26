package com.ryuqq.domain.feedbackqueue.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import java.util.Map;

/**
 * InvalidFeedbackStatusTransitionException - 유효하지 않은 피드백 상태 전이 예외
 *
 * @author ryu-qqq
 */
public class InvalidFeedbackStatusTransitionException extends DomainException {

    public InvalidFeedbackStatusTransitionException(
            Long feedbackQueueId, FeedbackStatus currentStatus, String attemptedAction) {
        super(
                FeedbackQueueErrorCode.INVALID_STATUS_TRANSITION,
                String.format(
                        "Cannot %s feedback %d in status %s",
                        attemptedAction, feedbackQueueId, currentStatus),
                Map.of(
                        "feedbackQueueId", feedbackQueueId,
                        "currentStatus", currentStatus.name(),
                        "attemptedAction", attemptedAction));
    }
}
