package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * OnboardingContextsResult - MCP Onboarding Context 조회 결과
 *
 * <p>get_onboarding_context Tool 응답용 온보딩 컨텍스트 목록 결과입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param contexts 온보딩 컨텍스트 목록
 * @param totalCount 전체 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record OnboardingContextsResult(List<OnboardingResult> contexts, int totalCount) {}
