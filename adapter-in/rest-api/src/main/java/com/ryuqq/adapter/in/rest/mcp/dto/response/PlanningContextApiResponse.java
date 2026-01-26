package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PlanningContextApiResponse - Planning Context 조회 응답 DTO
 *
 * <p>개발 계획 수립에 필요한 컨텍스트 정보를 담습니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param techStack 기술 스택 정보
 * @param architecture 아키텍처 정보
 * @param layers 레이어 목록 (모듈 및 패키지 포함)
 * @param summary 요약 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Planning Context 조회 응답")
public record PlanningContextApiResponse(
        @Schema(description = "기술 스택 정보") TechStackSummaryApiResponse techStack,
        @Schema(description = "아키텍처 정보") ArchitectureSummaryApiResponse architecture,
        @Schema(description = "레이어 목록") List<LayerWithModulesApiResponse> layers,
        @Schema(description = "요약 정보") PlanningContextSummaryApiResponse summary) {}
