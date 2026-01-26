package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TechStackSummaryApiResponse - 기술 스택 요약 정보
 *
 * @param id 기술 스택 ID
 * @param name 기술 스택 이름
 * @param description 기술 스택 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "기술 스택 요약 정보")
public record TechStackSummaryApiResponse(
        @Schema(description = "기술 스택 ID", example = "1") Long id,
        @Schema(description = "기술 스택 이름", example = "Spring Boot 3.5.x + Java 21") String name,
        @Schema(description = "기술 스택 설명", example = "현재 프로젝트 기술 스택") String description) {}
