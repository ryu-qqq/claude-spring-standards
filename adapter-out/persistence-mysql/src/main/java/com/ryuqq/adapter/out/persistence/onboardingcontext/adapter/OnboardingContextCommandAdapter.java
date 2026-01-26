package com.ryuqq.adapter.out.persistence.onboardingcontext.adapter;

import com.ryuqq.adapter.out.persistence.onboardingcontext.entity.OnboardingContextJpaEntity;
import com.ryuqq.adapter.out.persistence.onboardingcontext.mapper.OnboardingContextEntityMapper;
import com.ryuqq.adapter.out.persistence.onboardingcontext.repository.OnboardingContextJpaRepository;
import com.ryuqq.application.onboardingcontext.port.out.OnboardingContextCommandPort;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextCommandAdapter - OnboardingContext 명령 어댑터
 *
 * <p>OnboardingContextCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextCommandAdapter implements OnboardingContextCommandPort {

    private final OnboardingContextJpaRepository repository;
    private final OnboardingContextEntityMapper mapper;

    public OnboardingContextCommandAdapter(
            OnboardingContextJpaRepository repository, OnboardingContextEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * OnboardingContext 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param onboardingContext 영속화할 OnboardingContext
     * @return 영속화된 OnboardingContext ID
     */
    @Override
    public Long persist(OnboardingContext onboardingContext) {
        OnboardingContextJpaEntity entity = mapper.toEntity(onboardingContext);
        OnboardingContextJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
