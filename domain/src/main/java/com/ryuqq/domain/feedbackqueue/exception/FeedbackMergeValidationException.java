package com.ryuqq.domain.feedbackqueue.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import java.util.Map;

/**
 * FeedbackMergeValidationException - 피드백 병합 검증 실패 예외
 *
 * <p>병합 시점 검증에서 실패할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class FeedbackMergeValidationException extends DomainException {

    public FeedbackMergeValidationException(
            FeedbackTargetType targetType, FeedbackType feedbackType, String reason) {
        super(
                FeedbackQueueErrorCode.MERGE_VALIDATION_FAILED,
                String.format(
                        "Merge validation failed for %s/%s: %s",
                        targetType.name(), feedbackType.name(), reason),
                Map.of(
                        "targetType", targetType.name(),
                        "feedbackType", feedbackType.name(),
                        "reason", reason));
    }
}
