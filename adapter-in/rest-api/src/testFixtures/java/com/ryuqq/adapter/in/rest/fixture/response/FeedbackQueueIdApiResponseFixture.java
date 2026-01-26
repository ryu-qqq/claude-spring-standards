package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueIdApiResponse;

/**
 * FeedbackQueueIdApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class FeedbackQueueIdApiResponseFixture {

    private FeedbackQueueIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static FeedbackQueueIdApiResponse valid() {
        return FeedbackQueueIdApiResponse.of(1L);
    }

    public static FeedbackQueueIdApiResponse withId(Long id) {
        return FeedbackQueueIdApiResponse.of(id);
    }
}
