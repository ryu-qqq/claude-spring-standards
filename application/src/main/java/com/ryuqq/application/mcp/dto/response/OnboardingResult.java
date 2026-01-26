package com.ryuqq.application.mcp.dto.response;

/**
 * OnboardingResult - MCP Onboarding Context 결과 DTO
 *
 * <p>온보딩 컨텍스트 정보를 담는 결과 DTO입니다.
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
public record OnboardingResult(
        Long id, String contextType, String title, String content, int priority) {}
