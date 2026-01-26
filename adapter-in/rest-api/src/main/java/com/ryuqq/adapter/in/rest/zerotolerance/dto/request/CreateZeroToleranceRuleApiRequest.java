package com.ryuqq.adapter.in.rest.zerotolerance.dto.request;

import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateZeroToleranceRuleApiRequest - Zero-Tolerance 규칙 생성 API Request
 *
 * <p>Zero-Tolerance 규칙 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param ruleId CodingRule ID (필수)
 * @param type Zero-Tolerance 타입 (SECURITY, ARCHITECTURE, CODE_QUALITY)
 * @param detectionPattern 탐지 패턴 (정규식 또는 AST 패턴)
 * @param detectionType 탐지 방식 (REGEX, AST, ARCHUNIT)
 * @param autoRejectPr PR 자동 거부 여부
 * @param errorMessage 위반 시 표시할 에러 메시지
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Zero-Tolerance 규칙 생성 요청")
public record CreateZeroToleranceRuleApiRequest(
        @Schema(description = "CodingRule ID", example = "1") @NotNull(message = "ruleId는 필수입니다")
                Long ruleId,
        @Schema(description = "Zero-Tolerance 타입", example = "ARCHITECTURE")
                @NotBlank(message = "type은 필수입니다")
                @Size(max = 50, message = "type은 50자 이내여야 합니다")
                String type,
        @Schema(description = "탐지 패턴 (정규식 또는 AST 패턴)", example = "@(Data|Getter|Setter)")
                @NotBlank(message = "detectionPattern은 필수입니다")
                @Size(max = 2000, message = "detectionPattern은 2000자 이내여야 합니다")
                String detectionPattern,
        @Schema(description = "탐지 방식", example = "REGEX") @NotNull(message = "detectionType은 필수입니다")
                DetectionType detectionType,
        @Schema(description = "PR 자동 거부 여부", example = "true")
                @NotNull(message = "autoRejectPr은 필수입니다")
                Boolean autoRejectPr,
        @Schema(description = "위반 시 표시할 에러 메시지", example = "Lombok 어노테이션 사용 금지 - Domain Layer")
                @NotBlank(message = "errorMessage는 필수입니다")
                @Size(max = 1000, message = "errorMessage는 1000자 이내여야 합니다")
                String errorMessage) {}
