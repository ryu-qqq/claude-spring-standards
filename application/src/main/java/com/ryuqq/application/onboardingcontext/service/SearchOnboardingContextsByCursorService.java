package com.ryuqq.application.onboardingcontext.service;

import com.ryuqq.application.onboardingcontext.assembler.OnboardingContextAssembler;
import com.ryuqq.application.onboardingcontext.dto.query.OnboardingContextSearchParams;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextSliceResult;
import com.ryuqq.application.onboardingcontext.factory.query.OnboardingContextQueryFactory;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextReadManager;
import com.ryuqq.application.onboardingcontext.port.in.SearchOnboardingContextsByCursorUseCase;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchOnboardingContextsByCursorService - OnboardingContext 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchOnboardingContextsByCursorUseCase를 구현합니다.
 *
 * <p>TechStack ID, ContextType 필터를 지원하여 OnboardingContext 목록을 커서 기반으로 조회합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchOnboardingContextsByCursorService
        implements SearchOnboardingContextsByCursorUseCase {

    private final OnboardingContextReadManager onboardingContextReadManager;
    private final OnboardingContextQueryFactory onboardingContextQueryFactory;
    private final OnboardingContextAssembler onboardingContextAssembler;

    public SearchOnboardingContextsByCursorService(
            OnboardingContextReadManager onboardingContextReadManager,
            OnboardingContextQueryFactory onboardingContextQueryFactory,
            OnboardingContextAssembler onboardingContextAssembler) {
        this.onboardingContextReadManager = onboardingContextReadManager;
        this.onboardingContextQueryFactory = onboardingContextQueryFactory;
        this.onboardingContextAssembler = onboardingContextAssembler;
    }

    @Override
    public OnboardingContextSliceResult execute(OnboardingContextSearchParams searchParams) {
        OnboardingContextSliceCriteria criteria =
                onboardingContextQueryFactory.createSliceCriteria(searchParams);

        List<OnboardingContext> onboardingContexts =
                onboardingContextReadManager.findBySliceCriteria(criteria);
        return onboardingContextAssembler.toSliceResult(onboardingContexts, searchParams.size());
    }
}
