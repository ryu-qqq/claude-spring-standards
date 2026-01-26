package com.ryuqq.adapter.in.rest.onboardingcontext.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OnboardingContextApiResponse - OnboardingContext 조회 API Response
 *
 * <p>OnboardingContext 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param id OnboardingContext ID
 * @param techStackId TechStack ID (FK)
 * @param architectureId Architecture ID (FK, nullable)
 * @param contextType 컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE, RULES_INDEX, MCP_USAGE)
 * @param title 컨텍스트 제목
 * @param content 컨텍스트 내용
 * @param priority 온보딩 시 표시 순서
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "OnboardingContext 조회 응답")
public record OnboardingContextApiResponse(
        @Schema(description = "OnboardingContext ID", example = "1") Long id,
        @Schema(description = "TechStack ID", example = "1") Long techStackId,
        @Schema(description = "Architecture ID", example = "1", nullable = true)
                Long architectureId,
        @Schema(description = "컨텍스트 타입", example = "SUMMARY") String contextType,
        @Schema(description = "컨텍스트 제목", example = "프로젝트 개요") String title,
        @Schema(description = "컨텍스트 내용", example = "# 프로젝트 개요...") String content,
        @Schema(description = "온보딩 시 표시 순서", example = "0", nullable = true) Integer priority,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
