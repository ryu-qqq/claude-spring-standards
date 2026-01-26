package com.ryuqq.application.mcp.port.in;

import com.ryuqq.application.mcp.dto.query.GetOnboardingQuery;
import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;

/**
 * GetOnboardingForMcpUseCase - MCP get_onboarding_context용 Onboarding 조회 UseCase
 *
 * <p>MCP Tool에서 온보딩 컨텍스트를 조회할 때 사용하는 Port-In 인터페이스입니다.
 *
 * <p>PRT-001: Port-In은 UseCase 인터페이스.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetOnboardingForMcpUseCase {

    /**
     * 온보딩 컨텍스트 목록 조회
     *
     * @param query 조회 조건 (기술 스택, 아키텍처, 컨텍스트 타입)
     * @return 온보딩 컨텍스트 목록
     */
    OnboardingContextsResult execute(GetOnboardingQuery query);
}
