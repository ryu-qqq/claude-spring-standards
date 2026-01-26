package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;

/**
 * FeedbackQueueApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class FeedbackQueueApiResponseFixture {

    private FeedbackQueueApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static FeedbackQueueApiResponse valid() {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\",\"name\":\"Lombok 사용 금지\"}",
                "PENDING_LLM",
                null,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T09:00:00+09:00");
    }

    public static FeedbackQueueApiResponse withStatus(String status) {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\"}",
                status,
                null,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T09:00:00+09:00");
    }

    public static FeedbackQueueApiResponse llmApproved() {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\"}",
                "LLM_APPROVED",
                null,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T10:00:00+09:00");
    }

    public static FeedbackQueueApiResponse llmRejected() {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\"}",
                "LLM_REJECTED",
                "Invalid feedback format",
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T10:00:00+09:00");
    }

    public static FeedbackQueueApiResponse humanApproved() {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "MEDIUM",
                "{\"code\":\"AGG-001\"}",
                "HUMAN_APPROVED",
                null,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T11:00:00+09:00");
    }

    public static FeedbackQueueApiResponse humanRejected() {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "MEDIUM",
                "{\"code\":\"AGG-001\"}",
                "HUMAN_REJECTED",
                "Does not meet quality standards",
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T11:00:00+09:00");
    }

    public static FeedbackQueueApiResponse merged() {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\"}",
                "MERGED",
                null,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T12:00:00+09:00");
    }

    public static FeedbackQueueApiResponse withId(Long id) {
        return new FeedbackQueueApiResponse(
                id,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\"}",
                "PENDING_LLM",
                null,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T09:00:00+09:00");
    }

    public static FeedbackQueueApiResponse withReviewNotes(String reviewNotes) {
        return new FeedbackQueueApiResponse(
                1L,
                "CODING_RULE",
                1L,
                "CREATE",
                "LOW",
                "{\"code\":\"AGG-001\"}",
                "LLM_REJECTED",
                reviewNotes,
                "2024-01-01T09:00:00+09:00",
                "2024-01-01T10:00:00+09:00");
    }
}
