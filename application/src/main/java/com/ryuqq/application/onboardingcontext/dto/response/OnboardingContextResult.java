package com.ryuqq.application.onboardingcontext.dto.response;

import java.time.Instant;

/**
 * OnboardingContextResult - OnboardingContext 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 OnboardingContext 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param id OnboardingContext ID
 * @param techStackId 기술 스택 ID (FK)
 * @param architectureId 아키텍처 ID (FK, nullable)
 * @param contextType 컨텍스트 타입
 * @param title 컨텍스트 제목
 * @param content 컨텍스트 내용
 * @param priority 온보딩 시 표시 순서
 * @param deleted 삭제 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record OnboardingContextResult(
        Long id,
        Long techStackId,
        Long architectureId,
        String contextType,
        String title,
        String content,
        Integer priority,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt) {}
