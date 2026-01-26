package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.UpdateResourceTemplateApiRequest;

/**
 * UpdateResourceTemplateApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateResourceTemplateApiRequestFixture {

    private UpdateResourceTemplateApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateResourceTemplateApiRequest valid() {
        return new UpdateResourceTemplateApiRequest(
                "DOMAIN",
                "src/main/java/Order.java",
                "JAVA",
                "Order Aggregate",
                "public class Order {}",
                true);
    }

    public static UpdateResourceTemplateApiRequest invalidWithBlankCategory() {
        return new UpdateResourceTemplateApiRequest("", "path", "JAVA", "desc", "content", true);
    }

    public static UpdateResourceTemplateApiRequest invalidWithNullDescription() {
        return new UpdateResourceTemplateApiRequest(
                "DOMAIN", "path", "JAVA", null, "content", true);
    }

    public static UpdateResourceTemplateApiRequest invalidWithBlankFilePath() {
        return new UpdateResourceTemplateApiRequest("DOMAIN", "", "JAVA", "desc", "content", true);
    }
}
