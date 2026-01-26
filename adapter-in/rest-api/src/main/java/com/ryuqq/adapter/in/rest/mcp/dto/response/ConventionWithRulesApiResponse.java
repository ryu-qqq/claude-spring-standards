package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ConventionWithRulesApiResponse - 규칙을 포함한 컨벤션 응답
 *
 * @param id 컨벤션 ID
 * @param name 컨벤션 이름
 * @param description 설명
 * @param codingRules 코딩 규칙 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "규칙을 포함한 컨벤션")
public record ConventionWithRulesApiResponse(
        @Schema(description = "컨벤션 ID", example = "1") Long id,
        @Schema(description = "컨벤션 이름", example = "Domain Layer Convention") String name,
        @Schema(description = "설명", example = "도메인 레이어 코딩 컨벤션") String description,
        @Schema(description = "코딩 규칙 목록") List<CodingRuleWithDetailsApiResponse> codingRules) {}
