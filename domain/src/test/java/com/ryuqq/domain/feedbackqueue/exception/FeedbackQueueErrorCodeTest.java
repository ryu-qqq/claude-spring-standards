package com.ryuqq.domain.feedbackqueue.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FeedbackQueueErrorCode 테스트")
class FeedbackQueueErrorCodeTest {

    @Test
    @DisplayName("FEEDBACK_QUEUE_NOT_FOUND 에러 코드 확인")
    void feedbackQueueNotFound_ShouldHaveCorrectValues() {
        // given
        FeedbackQueueErrorCode errorCode = FeedbackQueueErrorCode.FEEDBACK_QUEUE_NOT_FOUND;

        // then
        assertThat(errorCode.getCode()).isEqualTo("FEEDBACK_QUEUE-001");
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
        assertThat(errorCode.getMessage()).isEqualTo("FeedbackQueue not found");
    }

    @Test
    @DisplayName("INVALID_STATUS_TRANSITION 에러 코드 확인")
    void invalidStatusTransition_ShouldHaveCorrectValues() {
        // given
        FeedbackQueueErrorCode errorCode = FeedbackQueueErrorCode.INVALID_STATUS_TRANSITION;

        // then
        assertThat(errorCode.getCode()).isEqualTo("FEEDBACK_QUEUE-002");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid feedback status transition");
    }
}
