package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModuleSummaryApiResponse - 모듈 요약 정보
 *
 * @param id 모듈 ID
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param layer 레이어 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "모듈 요약 정보")
public record ModuleSummaryApiResponse(
        @Schema(description = "모듈 ID", example = "1") Long id,
        @Schema(description = "모듈 이름", example = "domain-core") String name,
        @Schema(description = "모듈 설명", example = "도메인 핵심 모듈") String description,
        @Schema(description = "레이어 정보") LayerSummaryApiResponse layer) {}
