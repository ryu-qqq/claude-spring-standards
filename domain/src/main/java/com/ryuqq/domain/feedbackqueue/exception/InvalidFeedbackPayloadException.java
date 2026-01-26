package com.ryuqq.domain.feedbackqueue.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.Map;

/**
 * InvalidFeedbackPayloadException - 유효하지 않은 피드백 페이로드 예외
 *
 * <p>입력 시점 검증에서 페이로드가 유효하지 않을 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class InvalidFeedbackPayloadException extends DomainException {

    public InvalidFeedbackPayloadException(
            FeedbackTargetType targetType, String feedbackType, String reason) {
        super(
                FeedbackQueueErrorCode.INVALID_PAYLOAD,
                String.format(
                        "Invalid payload for %s/%s: %s", targetType.name(), feedbackType, reason),
                Map.of(
                        "targetType", targetType.name(),
                        "feedbackType", feedbackType,
                        "reason", reason));
    }
}
