package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.SearchFeedbacksCursorApiRequest;
import java.util.List;

/**
 * SearchFeedbacksCursorApiRequestFixture - SearchFeedbacksCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchFeedbacksCursorApiRequestFixture {

    private SearchFeedbacksCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchFeedbacksCursorApiRequest valid() {
        return new SearchFeedbacksCursorApiRequest(
                null,
                20,
                List.of("PENDING", "LLM_APPROVED"),
                List.of("CODING_RULE"),
                List.of("ADD", "MODIFY"),
                List.of("SAFE", "MEDIUM"),
                List.of("LLM_APPROVE", "HUMAN_REJECT"));
    }

    public static SearchFeedbacksCursorApiRequest withNullSize() {
        return new SearchFeedbacksCursorApiRequest(null, null, null, null, null, null, null);
    }

    public static SearchFeedbacksCursorApiRequest withZeroSize() {
        return new SearchFeedbacksCursorApiRequest(null, 0, null, null, null, null, null);
    }

    public static SearchFeedbacksCursorApiRequest withAllFilters() {
        return valid();
    }
}
