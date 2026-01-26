package com.ryuqq.application.mcp.service;

import com.ryuqq.application.mcp.assembler.OnboardingResultAssembler;
import com.ryuqq.application.mcp.dto.query.GetOnboardingQuery;
import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;
import com.ryuqq.application.mcp.port.in.GetOnboardingForMcpUseCase;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextReadManager;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetOnboardingForMcpService - MCP get_onboarding_context용 Onboarding 조회 서비스
 *
 * <p>MCP Tool에서 온보딩 컨텍스트를 조회할 때 사용하는 서비스입니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-003: ReadManager를 통한 조회 (Port 직접 사용 금지).
 *
 * <p>SVC-005: Assembler를 통한 DTO 변환.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class GetOnboardingForMcpService implements GetOnboardingForMcpUseCase {

    private final OnboardingContextReadManager onboardingContextReadManager;
    private final OnboardingResultAssembler onboardingResultAssembler;

    /**
     * GetOnboardingForMcpService 생성자
     *
     * @param onboardingContextReadManager OnboardingContext 조회 Manager
     * @param onboardingResultAssembler OnboardingResult 변환 Assembler
     */
    public GetOnboardingForMcpService(
            OnboardingContextReadManager onboardingContextReadManager,
            OnboardingResultAssembler onboardingResultAssembler) {
        this.onboardingContextReadManager = onboardingContextReadManager;
        this.onboardingResultAssembler = onboardingResultAssembler;
    }

    /**
     * 온보딩 컨텍스트 목록 조회
     *
     * @param query 조회 조건 (기술 스택, 아키텍처, 컨텍스트 타입)
     * @return 온보딩 컨텍스트 목록
     */
    @Override
    public OnboardingContextsResult execute(GetOnboardingQuery query) {
        TechStackId techStackId = TechStackId.of(query.techStackId());

        List<ContextType> contextTypes =
                query.contextTypes() == null || query.contextTypes().isEmpty()
                        ? null
                        : query.contextTypes().stream().map(ContextType::valueOf).toList();

        List<OnboardingContext> contexts =
                onboardingContextReadManager.findForMcp(
                        techStackId, query.architectureId(), contextTypes);

        return onboardingResultAssembler.toOnboardingContextsResult(contexts);
    }
}
