package com.ryuqq.adapter.in.rest.mcp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * GetOnboardingApiRequest - MCP Onboarding Context 조회 요청 DTO
 *
 * <p>get_onboarding_context Tool에서 온보딩 컨텍스트를 조회할 때 사용합니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param techStackId 기술 스택 ID (필수)
 * @param architectureId 아키텍처 ID (선택)
 * @param contextTypes 컨텍스트 타입 목록 (선택, 예: SUMMARY, ZERO_TOLERANCE)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "MCP Onboarding Context 조회 요청")
public record GetOnboardingApiRequest(
        @Schema(description = "기술 스택 ID", example = "1") @NotNull(message = "techStackId는 필수입니다")
                Long techStackId,
        @Schema(description = "아키텍처 ID (선택)", example = "1") Long architectureId,
        @Schema(
                        description = "컨텍스트 타입 목록",
                        example =
                                "[\"SUMMARY\", \"ZERO_TOLERANCE\", \"RULES_INDEX\", \"MCP_USAGE\"]")
                List<String> contextTypes) {}
