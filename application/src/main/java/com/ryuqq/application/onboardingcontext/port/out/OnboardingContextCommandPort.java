package com.ryuqq.application.onboardingcontext.port.out;

import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;

/**
 * OnboardingContextCommandPort - OnboardingContext 명령 Port
 *
 * <p>영속성 계층으로의 OnboardingContext CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface OnboardingContextCommandPort {

    /**
     * OnboardingContext 영속화 (생성/수정/삭제)
     *
     * @param onboardingContext 영속화할 OnboardingContext
     * @return 영속화된 OnboardingContext ID
     */
    Long persist(OnboardingContext onboardingContext);
}
