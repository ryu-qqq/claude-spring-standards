package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ValidationContextApiResponse - Validation Context 조회 응답 DTO
 *
 * <p>코드 검증에 필요한 Zero-Tolerance + Checklist 정보를 담습니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param zeroToleranceRules Zero-Tolerance 규칙 목록
 * @param checklist 체크리스트 항목 목록
 * @param summary 요약 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Validation Context 조회 응답")
public record ValidationContextApiResponse(
        @Schema(description = "Zero-Tolerance 규칙 목록")
                List<ZeroToleranceRuleApiResponse> zeroToleranceRules,
        @Schema(description = "체크리스트 항목 목록") List<ChecklistItemApiResponse> checklist,
        @Schema(description = "요약 정보") ValidationContextSummaryApiResponse summary) {}
