package com.ryuqq.adapter.in.rest.layerdependency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateLayerDependencyRuleApiRequest - LayerDependencyRule 생성 API Request
 *
 * <p>LayerDependencyRule 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull/@Nullable 명시 필수.
 *
 * <p>DTO-003: *ApiRequest 네이밍, @Schema 설명 필수.
 *
 * @param fromLayer 소스 레이어 (DOMAIN, APPLICATION, ADAPTER_IN, ADAPTER_OUT, COMMON, INFRASTRUCTURE)
 * @param toLayer 타겟 레이어
 * @param dependencyType 의존성 타입 (ALLOWED, FORBIDDEN, CONDITIONAL)
 * @param conditionDescription 조건 설명 (CONDITIONAL인 경우)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreateLayerDependencyRuleApiRequest(
        @NotBlank(message = "fromLayer는 필수입니다") @Schema(description = "소스 레이어", example = "DOMAIN")
                String fromLayer,
        @NotBlank(message = "toLayer는 필수입니다")
                @Schema(description = "타겟 레이어", example = "APPLICATION")
                String toLayer,
        @NotBlank(message = "dependencyType은 필수입니다")
                @Schema(description = "의존성 타입", example = "ALLOWED")
                String dependencyType,
        @Nullable
                @Size(max = 2000, message = "conditionDescription은 2000자 이내여야 합니다")
                @Schema(description = "조건 설명 (CONDITIONAL인 경우)", example = "특정 조건에서만 허용")
                String conditionDescription) {}
