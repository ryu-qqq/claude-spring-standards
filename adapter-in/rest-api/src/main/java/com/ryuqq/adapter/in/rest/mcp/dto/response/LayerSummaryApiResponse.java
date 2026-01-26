package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LayerSummaryApiResponse - 레이어 요약 정보
 *
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "레이어 요약 정보")
public record LayerSummaryApiResponse(
        @Schema(description = "레이어 코드", example = "DOMAIN") String code,
        @Schema(description = "레이어 이름", example = "Domain Layer") String name) {}
