package com.ryuqq.application.onboardingcontext.dto.command;

/**
 * CreateOnboardingContextCommand - OnboardingContext 생성 Command DTO
 *
 * <p>OnboardingContext 생성에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-002: 생성용은 Create{Domain}Command 네이밍.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param techStackId 기술 스택 ID (FK)
 * @param architectureId 아키텍처 ID (FK, nullable)
 * @param contextType 컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE, RULES_INDEX, MCP_USAGE)
 * @param title 컨텍스트 제목
 * @param content 컨텍스트 내용 (Markdown 지원)
 * @param priority 온보딩 시 표시 순서 (낮을수록 먼저)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreateOnboardingContextCommand(
        Long techStackId,
        Long architectureId,
        String contextType,
        String title,
        String content,
        Integer priority) {}
