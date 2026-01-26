package com.ryuqq.application.onboardingcontext.dto.command;

/**
 * UpdateOnboardingContextCommand - OnboardingContext 수정 Command DTO
 *
 * <p>OnboardingContext 수정에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-003: 수정용은 Update{Domain}Command 네이밍.
 *
 * <p>CDTO-004: Update Command는 UpdateData 생성에 필요한 전체 필드 포함.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param id 수정 대상 OnboardingContext ID
 * @param contextType 컨텍스트 타입
 * @param title 컨텍스트 제목
 * @param content 컨텍스트 내용
 * @param priority 온보딩 시 표시 순서
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateOnboardingContextCommand(
        Long id, String contextType, String title, String content, Integer priority) {}
