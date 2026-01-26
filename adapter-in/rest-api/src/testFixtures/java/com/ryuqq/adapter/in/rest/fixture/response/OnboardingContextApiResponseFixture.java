package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.onboardingcontext.dto.response.OnboardingContextApiResponse;

/**
 * OnboardingContextApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OnboardingContextApiResponseFixture {

    private OnboardingContextApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static OnboardingContextApiResponse valid() {
        return new OnboardingContextApiResponse(
                1L,
                1L,
                1L,
                "SUMMARY",
                "프로젝트 개요",
                "# 프로젝트 개요\n\n이 프로젝트는 Spring Boot 3.5 기반 헥사고날 아키텍처입니다.",
                0,
                "2024-01-15T10:30:00Z",
                "2024-01-15T10:30:00Z");
    }

    public static OnboardingContextApiResponse validWithoutOptionalFields() {
        return new OnboardingContextApiResponse(
                2L,
                1L,
                null,
                "ZERO_TOLERANCE",
                "Zero-Tolerance 규칙",
                "# Zero-Tolerance 규칙",
                null,
                "2024-01-15T10:30:00Z",
                "2024-01-15T10:30:00Z");
    }
}
