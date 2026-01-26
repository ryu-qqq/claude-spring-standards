package com.ryuqq.adapter.in.rest.checklistitem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ChecklistItemApiResponse - ChecklistItem API Response DTO
 *
 * <p>ChecklistItem 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param id 체크리스트 항목 ID
 * @param ruleId 코딩 규칙 ID
 * @param sequenceOrder 순서
 * @param checkDescription 체크 설명
 * @param checkType 체크 타입 (AUTOMATED, MANUAL, SEMI_AUTO)
 * @param automationTool 자동화 도구 (nullable)
 * @param automationRuleId 자동화 규칙 ID (nullable)
 * @param critical 필수 여부
 * @param source 체크리스트 소스 (MANUAL, AGENT_FEEDBACK)
 * @param feedbackId 피드백 ID (nullable)
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "체크리스트 항목 응답 DTO")
public record ChecklistItemApiResponse(
        @Schema(description = "체크리스트 항목 ID", example = "1") Long id,
        @Schema(description = "코딩 규칙 ID", example = "1") Long ruleId,
        @Schema(description = "순서", example = "1") Integer sequenceOrder,
        @Schema(description = "체크 설명", example = "Lombok 어노테이션이 사용되지 않았는지 확인")
                String checkDescription,
        @Schema(description = "체크 타입", example = "AUTOMATED") String checkType,
        @Schema(description = "자동화 도구", example = "ArchUnit", nullable = true)
                String automationTool,
        @Schema(description = "자동화 규칙 ID", example = "noLombokInDomain", nullable = true)
                String automationRuleId,
        @Schema(description = "필수 여부", example = "true") Boolean critical,
        @Schema(description = "체크리스트 소스", example = "MANUAL") String source,
        @Schema(description = "피드백 ID", example = "1", nullable = true) Long feedbackId,
        @Schema(description = "생성 일시 (ISO 8601 형식)", example = "2024-01-15T10:30:00")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601 형식)", example = "2024-01-15T10:30:00")
                String updatedAt) {}
