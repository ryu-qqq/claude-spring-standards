package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.UpdateOnboardingContextApiRequest;

/**
 * UpdateOnboardingContextApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateOnboardingContextApiRequestFixture {

    private UpdateOnboardingContextApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateOnboardingContextApiRequest valid() {
        return new UpdateOnboardingContextApiRequest(
                "SUMMARY", "프로젝트 개요 (수정됨)", "# 프로젝트 개요\n\n수정된 내용입니다.", 0);
    }

    public static UpdateOnboardingContextApiRequest invalidWithBlankTitle() {
        return new UpdateOnboardingContextApiRequest("SUMMARY", "", "# 내용", 0);
    }
}
