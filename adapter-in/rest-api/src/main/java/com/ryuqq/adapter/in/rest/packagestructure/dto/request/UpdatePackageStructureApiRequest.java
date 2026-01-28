package com.ryuqq.adapter.in.rest.packagestructure.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UpdatePackageStructureApiRequest - PackageStructure 수정 API Request
 *
 * <p>PackageStructure 수정 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param pathPattern 경로 패턴 (예: {base}.domain.{bc}.aggregate)
 * @param description 설명 (필수)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "패키지 구조 수정 요청")
public record UpdatePackageStructureApiRequest(
        @Schema(description = "경로 패턴", example = "{base}.domain.{bc}.aggregate")
                @NotBlank(message = "pathPattern은 필수입니다")
                @Size(max = 300, message = "pathPattern은 300자 이내여야 합니다")
                String pathPattern,
        @Schema(description = "설명", example = "도메인 Aggregate 클래스를 위한 패키지 구조")
                @NotNull(message = "description은 필수입니다")
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                String description) {}
