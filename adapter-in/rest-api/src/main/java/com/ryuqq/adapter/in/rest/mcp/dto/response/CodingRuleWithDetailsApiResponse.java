package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * CodingRuleWithDetailsApiResponse - 상세 정보를 포함한 코딩 규칙 응답
 *
 * @param id 규칙 ID
 * @param code 규칙 코드
 * @param title 규칙 제목
 * @param description 설명
 * @param severity 심각도
 * @param classType 클래스 타입
 * @param examples 예시 목록
 * @param zeroTolerance Zero-Tolerance 정보 (nullable)
 * @param checklistItem 체크리스트 항목 정보 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상세 정보를 포함한 코딩 규칙")
public record CodingRuleWithDetailsApiResponse(
        @Schema(description = "규칙 ID", example = "24") Long id,
        @Schema(description = "규칙 코드", example = "DOM-AGG-001") String code,
        @Schema(description = "규칙 제목", example = "Lombok 사용 금지") String title,
        @Schema(description = "설명", example = "Aggregate에서 Lombok 어노테이션 사용 금지") String description,
        @Schema(description = "심각도", example = "CRITICAL") String severity,
        @Schema(description = "클래스 타입", example = "AGGREGATE") String classType,
        @Schema(description = "예시 목록") List<RuleExampleDetailApiResponse> examples,
        @Schema(description = "Zero-Tolerance 정보", nullable = true)
                ZeroToleranceDetailApiResponse zeroTolerance,
        @Schema(description = "체크리스트 항목 정보", nullable = true)
                ChecklistItemDetailApiResponse checklistItem) {}
