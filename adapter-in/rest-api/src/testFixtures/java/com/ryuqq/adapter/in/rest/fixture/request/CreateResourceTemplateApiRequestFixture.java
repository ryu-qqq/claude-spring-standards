package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.CreateResourceTemplateApiRequest;

/**
 * CreateResourceTemplateApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateResourceTemplateApiRequestFixture {

    private CreateResourceTemplateApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateResourceTemplateApiRequest valid() {
        return new CreateResourceTemplateApiRequest(
                1L,
                "DOMAIN",
                "src/main/java/Order.java",
                "JAVA",
                "Order Aggregate",
                "public class Order {}",
                true);
    }

    public static CreateResourceTemplateApiRequest validMinimal() {
        return new CreateResourceTemplateApiRequest(
                1L, "APPLICATION", "Service.java", "JAVA", null, null, null);
    }

    public static CreateResourceTemplateApiRequest invalidWithNullModuleId() {
        return new CreateResourceTemplateApiRequest(
                null, "DOMAIN", "path", "JAVA", null, null, null);
    }

    public static CreateResourceTemplateApiRequest invalidWithBlankCategory() {
        return new CreateResourceTemplateApiRequest(1L, "", "path", "JAVA", null, null, null);
    }

    public static CreateResourceTemplateApiRequest invalidWithLongCategory() {
        return new CreateResourceTemplateApiRequest(
                1L, "A".repeat(51), "path", "JAVA", null, null, null);
    }

    public static CreateResourceTemplateApiRequest invalidWithBlankFilePath() {
        return new CreateResourceTemplateApiRequest(1L, "DOMAIN", "", "JAVA", null, null, null);
    }

    public static CreateResourceTemplateApiRequest invalidWithLongFilePath() {
        return new CreateResourceTemplateApiRequest(
                1L, "DOMAIN", "A".repeat(501), "JAVA", null, null, null);
    }
}
