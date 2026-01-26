package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ZeroToleranceRuleApiResponse - Zero-Tolerance 규칙 응답
 *
 * @param ruleCode 규칙 코드
 * @param ruleTitle 규칙 제목
 * @param layer 레이어
 * @param classTypes 클래스 타입 목록
 * @param detectionPattern 탐지 패턴
 * @param detectionType 탐지 타입
 * @param autoRejectPr PR 자동 거부 여부
 * @param message 에러 메시지
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Zero-Tolerance 규칙")
public record ZeroToleranceRuleApiResponse(
        @Schema(description = "규칙 코드", example = "DOM-AGG-001") String ruleCode,
        @Schema(description = "규칙 제목", example = "Lombok 사용 금지") String ruleTitle,
        @Schema(description = "레이어", example = "DOMAIN") String layer,
        @Schema(
                        description = "클래스 타입 목록",
                        example = "[\"AGGREGATE\", \"ENTITY\", \"VALUE_OBJECT\"]")
                List<String> classTypes,
        @Schema(description = "탐지 패턴", example = "@(Data|Getter|Setter|Builder)\\b")
                String detectionPattern,
        @Schema(description = "탐지 타입", example = "REGEX") String detectionType,
        @Schema(description = "PR 자동 거부 여부", example = "true") boolean autoRejectPr,
        @Schema(description = "에러 메시지", example = "Domain 레이어에서 Lombok 어노테이션 사용 금지")
                String message) {}
