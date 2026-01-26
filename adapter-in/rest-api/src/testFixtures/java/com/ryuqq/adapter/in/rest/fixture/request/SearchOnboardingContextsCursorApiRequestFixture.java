package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.SearchOnboardingContextsCursorApiRequest;
import java.util.List;

/**
 * SearchOnboardingContextsCursorApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchOnboardingContextsCursorApiRequestFixture {

    private SearchOnboardingContextsCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static SearchOnboardingContextsCursorApiRequest valid() {
        return new SearchOnboardingContextsCursorApiRequest(null, 20, null, null, null);
    }

    public static SearchOnboardingContextsCursorApiRequest validWithCursor() {
        return new SearchOnboardingContextsCursorApiRequest("100", 20, null, null, null);
    }

    public static SearchOnboardingContextsCursorApiRequest validWithFilters() {
        return new SearchOnboardingContextsCursorApiRequest(
                null, 20, List.of(1L, 2L), List.of(1L), List.of("SUMMARY", "ZERO_TOLERANCE"));
    }

    public static SearchOnboardingContextsCursorApiRequest invalidWithSizeTooLarge() {
        return new SearchOnboardingContextsCursorApiRequest(null, 101, null, null, null);
    }
}
