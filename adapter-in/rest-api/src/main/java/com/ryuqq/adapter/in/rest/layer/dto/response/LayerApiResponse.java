package com.ryuqq.adapter.in.rest.layer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LayerApiResponse - Layer 조회 API Response
 *
 * <p>Layer 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param id Layer ID
 * @param architectureId Architecture ID (FK)
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명
 * @param orderIndex 정렬 순서
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Layer 조회 응답")
public record LayerApiResponse(
        @Schema(description = "Layer ID", example = "1") Long id,
        @Schema(description = "Architecture ID", example = "1") Long architectureId,
        @Schema(description = "레이어 코드", example = "DOMAIN") String code,
        @Schema(description = "레이어 이름", example = "Domain Layer") String name,
        @Schema(description = "레이어 설명", example = "비즈니스 로직을 담당하는 도메인 레이어") String description,
        @Schema(description = "정렬 순서", example = "1") int orderIndex,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
