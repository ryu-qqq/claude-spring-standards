package com.ryuqq.adapter.in.rest.classtype.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ClassTypeApiResponse - ClassType 조회 API Response
 *
 * <p>ClassType 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param id ClassType ID
 * @param categoryId ClassTypeCategory ID (FK)
 * @param code 클래스 타입 코드
 * @param name 클래스 타입 이름
 * @param description 클래스 타입 설명
 * @param orderIndex 정렬 순서
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ClassType 조회 응답")
public record ClassTypeApiResponse(
        @Schema(description = "ClassType ID", example = "1") Long id,
        @Schema(description = "ClassTypeCategory ID", example = "1") Long categoryId,
        @Schema(description = "클래스 타입 코드", example = "AGGREGATE") String code,
        @Schema(description = "클래스 타입 이름", example = "Aggregate") String name,
        @Schema(description = "클래스 타입 설명", example = "도메인 Aggregate Root 클래스") String description,
        @Schema(description = "정렬 순서", example = "1") int orderIndex,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
