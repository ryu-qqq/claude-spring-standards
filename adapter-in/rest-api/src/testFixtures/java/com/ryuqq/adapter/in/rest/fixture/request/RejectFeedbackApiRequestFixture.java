package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.RejectFeedbackApiRequest;

/**
 * RejectFeedbackApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RejectFeedbackApiRequestFixture {

    private RejectFeedbackApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static RejectFeedbackApiRequest valid() {
        return new RejectFeedbackApiRequest(
                "Invalid feedback content - does not follow coding standards");
    }

    public static RejectFeedbackApiRequest withNullReviewNotes() {
        return new RejectFeedbackApiRequest(null);
    }

    public static RejectFeedbackApiRequest withEmptyReviewNotes() {
        return new RejectFeedbackApiRequest("");
    }

    public static RejectFeedbackApiRequest invalidWithLongReviewNotes() {
        return new RejectFeedbackApiRequest("A".repeat(2001));
    }
}
