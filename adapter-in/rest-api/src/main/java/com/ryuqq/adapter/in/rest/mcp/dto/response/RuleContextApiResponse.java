package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * RuleContextApiResponse - 규칙 컨텍스트 응답
 *
 * @param conventions 컨벤션 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "규칙 컨텍스트")
public record RuleContextApiResponse(
        @Schema(description = "컨벤션 목록") List<ConventionWithRulesApiResponse> conventions) {}
