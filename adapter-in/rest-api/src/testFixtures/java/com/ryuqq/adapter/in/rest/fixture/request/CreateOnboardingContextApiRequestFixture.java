package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.CreateOnboardingContextApiRequest;

/**
 * CreateOnboardingContextApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateOnboardingContextApiRequestFixture {

    private CreateOnboardingContextApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateOnboardingContextApiRequest valid() {
        return new CreateOnboardingContextApiRequest(
                1L,
                1L,
                "SUMMARY",
                "프로젝트 개요",
                "# 프로젝트 개요\n\n이 프로젝트는 Spring Boot 3.5 기반 헥사고날 아키텍처입니다.",
                0);
    }

    public static CreateOnboardingContextApiRequest validWithoutArchitecture() {
        return new CreateOnboardingContextApiRequest(
                1L,
                null,
                "ZERO_TOLERANCE",
                "Zero-Tolerance 규칙",
                "# Zero-Tolerance 규칙\n\n절대 위반하면 안 되는 규칙 목록입니다.",
                1);
    }

    public static CreateOnboardingContextApiRequest invalidWithBlankContextType() {
        return new CreateOnboardingContextApiRequest(1L, 1L, "", "제목", "# 내용", 0);
    }

    public static CreateOnboardingContextApiRequest invalidWithNullTechStackId() {
        return new CreateOnboardingContextApiRequest(null, 1L, "SUMMARY", "제목", "# 내용", 0);
    }
}
