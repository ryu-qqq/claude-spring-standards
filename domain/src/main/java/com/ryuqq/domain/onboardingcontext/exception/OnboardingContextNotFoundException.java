package com.ryuqq.domain.onboardingcontext.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * OnboardingContextNotFoundException - 온보딩 컨텍스트 미존재 예외
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class OnboardingContextNotFoundException extends DomainException {

    public OnboardingContextNotFoundException(Long onboardingContextId) {
        super(
                OnboardingContextErrorCode.ONBOARDING_CONTEXT_NOT_FOUND,
                String.format("Onboarding context not found: %d", onboardingContextId),
                Map.of("onboardingContextId", onboardingContextId));
    }
}
