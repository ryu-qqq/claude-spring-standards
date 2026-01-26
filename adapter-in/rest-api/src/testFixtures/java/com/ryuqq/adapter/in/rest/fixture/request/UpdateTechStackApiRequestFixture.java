package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.techstack.dto.request.UpdateTechStackApiRequest;
import java.util.List;

/**
 * UpdateTechStackApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateTechStackApiRequestFixture {

    private UpdateTechStackApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateTechStackApiRequest valid() {
        return new UpdateTechStackApiRequest(
                "Spring Boot 3.5 with Java 21",
                "ACTIVE",
                "JAVA",
                "21",
                List.of("records", "sealed-classes"),
                "SPRING_BOOT",
                "3.5.0",
                List.of("spring-web", "spring-data-jpa"),
                "JVM",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    public static UpdateTechStackApiRequest invalidWithBlankName() {
        return new UpdateTechStackApiRequest(
                "",
                "ACTIVE",
                "JAVA",
                "21",
                List.of(),
                "SPRING_BOOT",
                "3.5.0",
                List.of(),
                "JVM",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }
}
