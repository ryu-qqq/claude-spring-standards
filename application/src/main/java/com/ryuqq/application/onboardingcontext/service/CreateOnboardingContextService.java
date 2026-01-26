package com.ryuqq.application.onboardingcontext.service;

import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.factory.command.OnboardingContextCommandFactory;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextPersistenceManager;
import com.ryuqq.application.onboardingcontext.port.in.CreateOnboardingContextUseCase;
import com.ryuqq.application.onboardingcontext.validator.OnboardingContextValidator;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Service;

/**
 * CreateOnboardingContextService - OnboardingContext 생성 서비스
 *
 * <p>CreateOnboardingContextUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class CreateOnboardingContextService implements CreateOnboardingContextUseCase {

    private final OnboardingContextValidator onboardingContextValidator;
    private final OnboardingContextCommandFactory onboardingContextCommandFactory;
    private final OnboardingContextPersistenceManager onboardingContextPersistenceManager;

    public CreateOnboardingContextService(
            OnboardingContextValidator onboardingContextValidator,
            OnboardingContextCommandFactory onboardingContextCommandFactory,
            OnboardingContextPersistenceManager onboardingContextPersistenceManager) {
        this.onboardingContextValidator = onboardingContextValidator;
        this.onboardingContextCommandFactory = onboardingContextCommandFactory;
        this.onboardingContextPersistenceManager = onboardingContextPersistenceManager;
    }

    @Override
    public Long execute(CreateOnboardingContextCommand command) {
        onboardingContextValidator.validateTechStackExists(TechStackId.of(command.techStackId()));

        OnboardingContext onboardingContext = onboardingContextCommandFactory.create(command);
        return onboardingContextPersistenceManager.persist(onboardingContext);
    }
}
