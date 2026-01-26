package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModuleContextApiResponse - Module Context 조회 응답 DTO
 *
 * <p>코드 생성에 필요한 Module 전체 컨텍스트 정보를 담습니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param module 모듈 정보
 * @param executionContext 실행 컨텍스트
 * @param ruleContext 규칙 컨텍스트
 * @param summary 요약 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Module Context 조회 응답")
public record ModuleContextApiResponse(
        @Schema(description = "모듈 정보") ModuleSummaryApiResponse module,
        @Schema(description = "실행 컨텍스트") ExecutionContextApiResponse executionContext,
        @Schema(description = "규칙 컨텍스트") RuleContextApiResponse ruleContext,
        @Schema(description = "요약 정보") ModuleContextSummaryApiResponse summary) {}
