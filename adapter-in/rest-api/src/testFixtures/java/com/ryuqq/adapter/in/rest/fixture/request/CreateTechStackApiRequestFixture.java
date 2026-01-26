package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.techstack.dto.request.CreateTechStackApiRequest;
import java.util.List;

/**
 * CreateTechStackApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateTechStackApiRequestFixture {

    private CreateTechStackApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateTechStackApiRequest valid() {
        return new CreateTechStackApiRequest(
                "Spring Boot 3.5 with Java 21",
                "JAVA",
                "21",
                List.of("records", "sealed-classes", "pattern-matching"),
                "SPRING_BOOT",
                "3.5.0",
                List.of("spring-web", "spring-data-jpa"),
                "JVM",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }

    public static CreateTechStackApiRequest invalidWithBlankName() {
        return new CreateTechStackApiRequest(
                "",
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

    public static CreateTechStackApiRequest invalidWithLongName() {
        return new CreateTechStackApiRequest(
                "A".repeat(101),
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

    public static CreateTechStackApiRequest invalidWithNullLanguageFeatures() {
        return new CreateTechStackApiRequest(
                "Spring Boot",
                "JAVA",
                "21",
                null,
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
