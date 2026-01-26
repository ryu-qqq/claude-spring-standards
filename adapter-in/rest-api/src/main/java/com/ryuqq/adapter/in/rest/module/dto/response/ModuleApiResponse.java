package com.ryuqq.adapter.in.rest.module.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModuleApiResponse - Module 조회 API Response
 *
 * <p>Module 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param moduleId 모듈 ID
 * @param layerId 레이어 ID
 * @param parentModuleId 부모 모듈 ID (nullable)
 * @param name 모듈 이름
 * @param description 설명
 * @param modulePath 모듈 파일 시스템 경로
 * @param buildIdentifier 빌드 시스템 식별자 (nullable)
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Module 조회 응답")
public record ModuleApiResponse(
        @Schema(description = "모듈 ID", example = "1") Long moduleId,
        @Schema(description = "레이어 ID", example = "1") Long layerId,
        @Schema(description = "부모 모듈 ID", example = "2", nullable = true) Long parentModuleId,
        @Schema(description = "모듈 이름", example = "adapter-in-rest-api") String name,
        @Schema(description = "설명", example = "REST API Adapter", nullable = true)
                String description,
        @Schema(description = "모듈 파일 시스템 경로", example = "adapter-in/rest-api") String modulePath,
        @Schema(description = "빌드 시스템 식별자", example = ":adapter-in:rest-api", nullable = true)
                String buildIdentifier,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
