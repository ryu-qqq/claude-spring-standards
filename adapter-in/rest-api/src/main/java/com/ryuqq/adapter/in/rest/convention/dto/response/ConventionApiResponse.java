package com.ryuqq.adapter.in.rest.convention.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ConventionApiResponse - Convention API 응답 DTO
 *
 * <p>REST API 응답용 Convention 정보입니다.
 *
 * <p>DTO-007: from() 메서드 금지 → Mapper에서 변환.
 *
 * <p>DTO-016: 날짜/시간 필드는 String 타입 (ISO 8601 포맷).
 *
 * @param id 컨벤션 ID
 * @param moduleId 모듈 ID
 * @param version 버전
 * @param description 설명
 * @param active 활성화 여부
 * @param createdAt 생성 시각 (ISO 8601 포맷)
 * @param updatedAt 수정 시각 (ISO 8601 포맷)
 * @author ryu-qqq
 */
@Schema(description = "컨벤션 정보")
public record ConventionApiResponse(
        @Schema(description = "컨벤션 ID", example = "1") Long id,
        @Schema(description = "모듈 ID", example = "1") Long moduleId,
        @Schema(description = "버전", example = "1.0.0") String version,
        @Schema(description = "설명", example = "Spring Boot 3.5 기반 도메인 레이어 코딩 컨벤션")
                String description,
        @Schema(description = "활성화 여부", example = "true") boolean active,
        @Schema(description = "생성 시각 (ISO 8601 포맷)", example = "2025-01-23T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601 포맷)", example = "2025-01-23T10:30:00+09:00")
                String updatedAt) {}
