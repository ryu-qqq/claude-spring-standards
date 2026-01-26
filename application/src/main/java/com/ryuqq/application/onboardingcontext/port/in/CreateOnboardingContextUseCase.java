package com.ryuqq.application.onboardingcontext.port.in;

import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;

/**
 * CreateOnboardingContextUseCase - OnboardingContext 생성 UseCase
 *
 * <p>새로운 OnboardingContext를 생성합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-006: Command UseCase는 동사 접두어 + UseCase 네이밍.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface CreateOnboardingContextUseCase {

    /**
     * OnboardingContext 생성 실행
     *
     * @param command 생성 Command
     * @return 생성된 OnboardingContext ID
     */
    Long execute(CreateOnboardingContextCommand command);
}
