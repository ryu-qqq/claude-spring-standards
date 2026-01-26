package com.ryuqq.adapter.in.rest.onboardingcontext.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdateOnboardingContextApiRequest - OnboardingContext 수정 API Request
 *
 * <p>OnboardingContext 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param contextType 컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE, RULES_INDEX, MCP_USAGE)
 * @param title 컨텍스트 제목
 * @param content 컨텍스트 내용 (Markdown 지원)
 * @param priority 온보딩 시 표시 순서 (낮을수록 먼저)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "OnboardingContext 수정 요청 DTO")
public record UpdateOnboardingContextApiRequest(
        @Schema(description = "컨텍스트 타입", example = "SUMMARY")
                @NotBlank(message = "contextType은 필수입니다")
                @Size(max = 50, message = "contextType은 50자 이내여야 합니다")
                String contextType,
        @Schema(description = "컨텍스트 제목", example = "프로젝트 개요")
                @NotBlank(message = "title은 필수입니다")
                @Size(max = 100, message = "title은 100자 이내여야 합니다")
                String title,
        @Schema(description = "컨텍스트 내용 (Markdown 지원)", example = "# 프로젝트 개요...")
                @NotBlank(message = "content는 필수입니다")
                String content,
        @Schema(description = "온보딩 시 표시 순서 (낮을수록 먼저)", example = "0", nullable = true)
                Integer priority) {}
