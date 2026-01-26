package com.ryuqq.domain.feedbackqueue.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * FeedbackQueueErrorCode - 피드백 큐 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum FeedbackQueueErrorCode implements ErrorCode {
    FEEDBACK_QUEUE_NOT_FOUND("FEEDBACK_QUEUE-001", 404, "FeedbackQueue not found"),
    INVALID_STATUS_TRANSITION("FEEDBACK_QUEUE-002", 400, "Invalid feedback status transition"),
    INVALID_PAYLOAD("FEEDBACK_QUEUE-003", 400, "Invalid feedback payload"),
    MERGE_VALIDATION_FAILED("FEEDBACK_QUEUE-004", 400, "Feedback merge validation failed");

    private final String code;
    private final int httpStatus;
    private final String message;

    FeedbackQueueErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
