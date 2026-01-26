package com.ryuqq.application.mcp.dto.query;

import java.util.List;

/**
 * GetOnboardingQuery - MCP Onboarding Context 조회 쿼리
 *
 * <p>get_onboarding_context Tool에서 사용할 온보딩 컨텍스트 조회 쿼리입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param techStackId 기술 스택 ID
 * @param architectureId 아키텍처 ID (nullable)
 * @param contextTypes 컨텍스트 타입 목록 (예: SUMMARY, ZERO_TOLERANCE, RULES_INDEX, MCP_USAGE)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record GetOnboardingQuery(
        Long techStackId, Long architectureId, List<String> contextTypes) {}
