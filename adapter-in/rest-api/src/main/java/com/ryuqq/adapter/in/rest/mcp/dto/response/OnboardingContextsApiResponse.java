package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * OnboardingContextsApiResponse - MCP Onboarding Context 조회 응답 DTO
 *
 * <p>get_onboarding_context Tool 응답용 온보딩 컨텍스트 목록입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param contexts 온보딩 컨텍스트 목록
 * @param totalCount 전체 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "온보딩 컨텍스트 목록 응답")
public record OnboardingContextsApiResponse(
        @Schema(description = "온보딩 컨텍스트 목록") List<OnboardingApiResponse> contexts,
        @Schema(description = "전체 개수", example = "4") int totalCount) {}
