package com.ryuqq.adapter.in.rest.packagepurpose.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdatePackagePurposeApiRequest - PackagePurpose 수정 API Request
 *
 * <p>PackagePurpose 수정 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull/@Nullable 명시 필수.
 *
 * <p>DTO-003: *ApiRequest 네이밍, @Schema 설명 필수.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param code 목적 코드
 * @param name 목적 이름
 * @param description 설명 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdatePackagePurposeApiRequest(
        @NotBlank(message = "code는 필수입니다")
                @Size(max = 50, message = "code는 50자 이내여야 합니다")
                @Schema(description = "목적 코드", example = "AGGREGATE")
                String code,
        @NotBlank(message = "name은 필수입니다")
                @Size(max = 100, message = "name은 100자 이내여야 합니다")
                @Schema(description = "목적 이름", example = "Aggregate Root")
                String name,
        @Nullable
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                @Schema(description = "설명", example = "DDD Aggregate Root 패키지")
                String description) {}
