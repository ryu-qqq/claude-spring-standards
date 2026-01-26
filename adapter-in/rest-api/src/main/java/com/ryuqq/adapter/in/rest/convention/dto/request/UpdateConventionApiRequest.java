package com.ryuqq.adapter.in.rest.convention.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UpdateConventionApiRequest - Convention 수정 API Request
 *
 * <p>Convention 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param moduleId 모듈 ID (Module 테이블 FK)
 * @param version 버전 (예: "1.0.0")
 * @param description 설명
 * @param active 활성화 여부
 * @author ryu-qqq
 */
@Schema(description = "컨벤션 수정 요청")
public record UpdateConventionApiRequest(
        @Schema(description = "모듈 ID (Module 테이블 FK)", example = "1")
                @NotNull(message = "moduleId는 필수입니다")
                Long moduleId,
        @Schema(description = "버전", example = "1.0.0")
                @NotBlank(message = "version은 필수입니다")
                @Size(max = 20, message = "version은 20자 이내여야 합니다")
                String version,
        @Schema(description = "설명", example = "Spring Boot 3.5 기반 도메인 레이어 코딩 컨벤션")
                @NotBlank(message = "description은 필수입니다")
                @Size(max = 1000, message = "description은 1000자 이내여야 합니다")
                String description,
        @Schema(description = "활성화 여부", example = "true") @NotNull(message = "active는 필수입니다")
                Boolean active) {}
