package com.ryuqq.application.onboardingcontext.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.factory.command.OnboardingContextCommandFactory;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextPersistenceManager;
import com.ryuqq.application.onboardingcontext.port.in.UpdateOnboardingContextUseCase;
import com.ryuqq.application.onboardingcontext.validator.OnboardingContextValidator;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContextUpdateData;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import org.springframework.stereotype.Service;

/**
 * UpdateOnboardingContextService - OnboardingContext 수정 서비스
 *
 * <p>UpdateOnboardingContextUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class UpdateOnboardingContextService implements UpdateOnboardingContextUseCase {

    private final OnboardingContextValidator onboardingContextValidator;
    private final OnboardingContextCommandFactory onboardingContextCommandFactory;
    private final OnboardingContextPersistenceManager onboardingContextPersistenceManager;

    public UpdateOnboardingContextService(
            OnboardingContextValidator onboardingContextValidator,
            OnboardingContextCommandFactory onboardingContextCommandFactory,
            OnboardingContextPersistenceManager onboardingContextPersistenceManager) {
        this.onboardingContextValidator = onboardingContextValidator;
        this.onboardingContextCommandFactory = onboardingContextCommandFactory;
        this.onboardingContextPersistenceManager = onboardingContextPersistenceManager;
    }

    @Override
    public void execute(UpdateOnboardingContextCommand command) {
        UpdateContext<OnboardingContextId, OnboardingContextUpdateData> context =
                onboardingContextCommandFactory.createUpdateContext(command);

        OnboardingContext onboardingContext =
                onboardingContextValidator.findExistingOrThrow(context.id());
        onboardingContext.update(context.updateData(), context.changedAt());
        onboardingContextPersistenceManager.persist(onboardingContext);
    }
}
