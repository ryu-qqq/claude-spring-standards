package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ZeroToleranceDetailApiResponse - Zero-Tolerance 상세 정보
 *
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 타입
 * @param autoRejectPr PR 자동 거부 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Zero-Tolerance 상세 정보")
public record ZeroToleranceDetailApiResponse(
        @Schema(description = "탐지 패턴", example = "@(Data|Getter|Setter|Builder)\\b")
                String detectionPattern,
        @Schema(description = "탐지 타입", example = "REGEX") String detectionType,
        @Schema(description = "PR 자동 거부 여부", example = "true") boolean autoRejectPr) {}
