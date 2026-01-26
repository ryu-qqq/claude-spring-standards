package com.ryuqq.adapter.in.rest.layerdependency.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LayerDependencyRuleIdApiResponse - LayerDependencyRule ID API Response DTO
 *
 * <p>LayerDependencyRule 생성 결과의 ID를 응답합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * @param layerDependencyRuleId 생성된 레이어 의존성 규칙 ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "레이어 의존성 규칙 ID 응답 DTO")
public record LayerDependencyRuleIdApiResponse(
        @Schema(description = "생성된 레이어 의존성 규칙 ID", example = "1") Long layerDependencyRuleId) {

    /**
     * ID로부터 응답 생성
     *
     * @param id 레이어 의존성 규칙 ID
     * @return LayerDependencyRuleIdApiResponse
     */
    public static LayerDependencyRuleIdApiResponse of(Long id) {
        return new LayerDependencyRuleIdApiResponse(id);
    }
}
