package com.ryuqq.adapter.in.rest.checklistitem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateChecklistItemApiRequest - ChecklistItem 생성 API Request
 *
 * <p>ChecklistItem 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param ruleId 코딩 규칙 ID (필수)
 * @param sequenceOrder 순서 (필수, 1 이상)
 * @param checkDescription 체크 설명 (필수, 최대 500자)
 * @param checkType 체크 타입 (AUTOMATED, MANUAL, SEMI_AUTO)
 * @param automationTool 자동화 도구 (nullable)
 * @param automationRuleId 자동화 규칙 ID (nullable)
 * @param isCritical 필수 여부 (기본값 false)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "체크리스트 항목 생성 요청 DTO")
public record CreateChecklistItemApiRequest(
        @Schema(description = "코딩 규칙 ID", example = "1") @NotNull(message = "ruleId는 필수입니다")
                Long ruleId,
        @Schema(description = "순서", example = "1")
                @NotNull(message = "sequenceOrder는 필수입니다")
                @Min(value = 1, message = "sequenceOrder는 1 이상이어야 합니다")
                Integer sequenceOrder,
        @Schema(description = "체크 설명", example = "Lombok 어노테이션이 사용되지 않았는지 확인")
                @NotBlank(message = "checkDescription은 필수입니다")
                @Size(max = 500, message = "checkDescription은 500자 이내여야 합니다")
                String checkDescription,
        @Schema(description = "체크 타입", example = "AUTOMATED")
                @NotBlank(message = "checkType은 필수입니다")
                @Size(max = 20, message = "checkType은 20자 이내여야 합니다")
                String checkType,
        @Schema(description = "자동화 도구", example = "ArchUnit", nullable = true)
                @Size(max = 50, message = "automationTool은 50자 이내여야 합니다")
                String automationTool,
        @Schema(description = "자동화 규칙 ID", example = "noLombokInDomain", nullable = true)
                @Size(max = 100, message = "automationRuleId는 100자 이내여야 합니다")
                String automationRuleId,
        @Schema(description = "필수 여부", example = "true", nullable = true) Boolean isCritical) {}
