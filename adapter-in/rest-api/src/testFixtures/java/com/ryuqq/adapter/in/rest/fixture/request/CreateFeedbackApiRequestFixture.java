package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.CreateFeedbackApiRequest;

/**
 * CreateFeedbackApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateFeedbackApiRequestFixture {

    private CreateFeedbackApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateFeedbackApiRequest valid() {
        return new CreateFeedbackApiRequest(
                "CODING_RULE", 1L, "CREATE", "{\"code\":\"AGG-001\",\"name\":\"Lombok 사용 금지\"}");
    }

    public static CreateFeedbackApiRequest validWithUpdate() {
        return new CreateFeedbackApiRequest(
                "CLASS_TEMPLATE",
                2L,
                "UPDATE",
                "{\"classType\":\"AGGREGATE\",\"namingPattern\":\"*Aggregate\"}");
    }

    public static CreateFeedbackApiRequest validWithDelete() {
        return new CreateFeedbackApiRequest(
                "RULE_EXAMPLE", 3L, "DELETE", "{\"reason\":\"Deprecated example\"}");
    }

    public static CreateFeedbackApiRequest invalidWithBlankTargetType() {
        return new CreateFeedbackApiRequest("", 1L, "CREATE", "{\"code\":\"AGG-001\"}");
    }

    public static CreateFeedbackApiRequest invalidWithNullTargetId() {
        return new CreateFeedbackApiRequest(
                "CODING_RULE", null, "CREATE", "{\"code\":\"AGG-001\"}");
    }

    public static CreateFeedbackApiRequest invalidWithBlankFeedbackType() {
        return new CreateFeedbackApiRequest("CODING_RULE", 1L, "", "{\"code\":\"AGG-001\"}");
    }

    public static CreateFeedbackApiRequest invalidWithBlankPayload() {
        return new CreateFeedbackApiRequest("CODING_RULE", 1L, "CREATE", "");
    }

    public static CreateFeedbackApiRequest invalidWithLongPayload() {
        return new CreateFeedbackApiRequest("CODING_RULE", 1L, "CREATE", "A".repeat(10001));
    }
}
