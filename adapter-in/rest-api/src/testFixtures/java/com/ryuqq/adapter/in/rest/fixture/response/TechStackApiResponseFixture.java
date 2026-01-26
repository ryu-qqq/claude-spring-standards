package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.techstack.dto.response.TechStackApiResponse;
import java.util.List;

/**
 * TechStackApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TechStackApiResponseFixture {

    private TechStackApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static TechStackApiResponse valid() {
        return new TechStackApiResponse(
                1L,
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
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
