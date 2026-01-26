package com.ryuqq.application.onboardingcontext.manager;

import com.ryuqq.application.onboardingcontext.port.out.OnboardingContextCommandPort;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OnboardingContextPersistenceManager - OnboardingContext 영속성 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextPersistenceManager {

    private final OnboardingContextCommandPort onboardingContextCommandPort;

    public OnboardingContextPersistenceManager(
            OnboardingContextCommandPort onboardingContextCommandPort) {
        this.onboardingContextCommandPort = onboardingContextCommandPort;
    }

    /**
     * OnboardingContext 영속화
     *
     * @param onboardingContext 영속화할 OnboardingContext
     * @return 영속화된 OnboardingContext ID
     */
    @Transactional
    public Long persist(OnboardingContext onboardingContext) {
        return onboardingContextCommandPort.persist(onboardingContext);
    }
}
