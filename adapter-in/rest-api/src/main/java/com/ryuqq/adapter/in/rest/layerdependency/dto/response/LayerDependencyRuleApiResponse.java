package com.ryuqq.adapter.in.rest.layerdependency.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LayerDependencyRuleApiResponse - LayerDependencyRule API Response DTO
 *
 * <p>LayerDependencyRule 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-003: @Schema 설명 필수.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param layerDependencyRuleId 레이어 의존성 규칙 ID
 * @param architectureId 아키텍처 ID
 * @param fromLayer 소스 레이어
 * @param toLayer 타겟 레이어
 * @param dependencyType 의존성 타입
 * @param conditionDescription 조건 설명
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerDependencyRuleApiResponse(
        @Schema(description = "레이어 의존성 규칙 ID", example = "1") Long layerDependencyRuleId,
        @Schema(description = "아키텍처 ID", example = "1") Long architectureId,
        @Schema(description = "소스 레이어", example = "DOMAIN") String fromLayer,
        @Schema(description = "타겟 레이어", example = "APPLICATION") String toLayer,
        @Schema(description = "의존성 타입", example = "ALLOWED") String dependencyType,
        @Schema(description = "조건 설명 (CONDITIONAL인 경우)") String conditionDescription,
        @Schema(description = "생성 일시 (ISO 8601 형식)", example = "2024-01-01T00:00:00Z")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601 형식)", example = "2024-01-01T00:00:00Z")
                String updatedAt) {}
