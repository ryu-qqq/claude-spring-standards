package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.classtype.dto.response.ClassTypeApiResponse;

/**
 * ClassTypeApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ClassTypeApiResponseFixture {

    private static final String DEFAULT_CREATED_AT = "2024-01-01T00:00:00Z";
    private static final String DEFAULT_UPDATED_AT = "2024-01-01T00:00:00Z";

    private ClassTypeApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ClassTypeApiResponse valid() {
        return new ClassTypeApiResponse(
                1L,
                1L,
                "AGGREGATE",
                "Aggregate",
                "도메인 Aggregate Root 클래스",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeApiResponse withId(Long id) {
        return new ClassTypeApiResponse(
                id,
                1L,
                "AGGREGATE",
                "Aggregate",
                "도메인 Aggregate Root 클래스",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeApiResponse valueObject() {
        return new ClassTypeApiResponse(
                2L,
                1L,
                "VALUE_OBJECT",
                "Value Object",
                "도메인 Value Object 클래스",
                2,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeApiResponse domainEvent() {
        return new ClassTypeApiResponse(
                3L,
                1L,
                "DOMAIN_EVENT",
                "Domain Event",
                "도메인 이벤트 클래스",
                3,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static ClassTypeApiResponse useCase() {
        return new ClassTypeApiResponse(
                4L,
                2L,
                "USE_CASE",
                "Use Case",
                "애플리케이션 Use Case 클래스",
                1,
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }
}
