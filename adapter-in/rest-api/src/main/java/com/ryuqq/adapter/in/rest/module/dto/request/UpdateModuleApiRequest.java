package com.ryuqq.adapter.in.rest.module.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdateModuleApiRequest - Module 수정 API Request
 *
 * <p>Module 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>Layer는 생성 시점에 결정되므로 수정 대상에서 제외됩니다.
 *
 * @param parentModuleId 부모 모듈 ID (nullable)
 * @param name 모듈 이름
 * @param description 설명
 * @param modulePath 모듈 파일 시스템 경로
 * @param buildIdentifier 빌드 시스템 식별자 (nullable)
 * @author ryu-qqq
 */
@Schema(description = "Module 수정 요청")
public record UpdateModuleApiRequest(
        @Schema(description = "부모 모듈 ID", example = "2", nullable = true) Long parentModuleId,
        @Schema(description = "모듈 이름", example = "order-domain")
                @NotBlank(message = "name은 필수입니다")
                @Size(max = 100, message = "name은 100자 이내여야 합니다")
                String name,
        @Schema(description = "모듈 설명", example = "주문 도메인 모듈", nullable = true)
                @Size(max = 500, message = "description은 500자 이내여야 합니다")
                String description,
        @Schema(description = "모듈 파일 시스템 경로", example = "domain/order")
                @NotBlank(message = "modulePath는 필수입니다")
                @Size(max = 200, message = "modulePath는 200자 이내여야 합니다")
                String modulePath,
        @Schema(description = "빌드 시스템 식별자", example = ":domain:order", nullable = true)
                @Size(max = 200, message = "buildIdentifier는 200자 이내여야 합니다")
                String buildIdentifier) {}
