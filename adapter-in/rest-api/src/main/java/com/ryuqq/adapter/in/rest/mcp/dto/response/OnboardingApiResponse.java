package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OnboardingApiResponse - MCP Onboarding Context 개별 응답 DTO
 *
 * <p>온보딩 컨텍스트 정보를 담습니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param id 온보딩 컨텍스트 ID
 * @param contextType 컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE, RULES_INDEX, MCP_USAGE)
 * @param title 제목
 * @param content 내용 (Markdown)
 * @param priority 우선순위
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "온보딩 컨텍스트 정보")
public record OnboardingApiResponse(
        @Schema(description = "온보딩 컨텍스트 ID", example = "1") Long id,
        @Schema(description = "컨텍스트 타입", example = "SUMMARY") String contextType,
        @Schema(description = "제목", example = "프로젝트 개요") String title,
        @Schema(description = "내용", example = "# 프로젝트 개요\n\n...") String content,
        @Schema(description = "우선순위", example = "0") int priority) {}
