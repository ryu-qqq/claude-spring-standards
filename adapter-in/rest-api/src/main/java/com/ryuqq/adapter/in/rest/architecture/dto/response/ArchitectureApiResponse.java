package com.ryuqq.adapter.in.rest.architecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ArchitectureApiResponse - Architecture 조회 API Response
 *
 * <p>Architecture 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param id Architecture ID
 * @param techStackId TechStack ID (FK)
 * @param name 아키텍처 이름
 * @param patternType 패턴 타입
 * @param patternDescription 패턴 설명
 * @param patternPrinciples 패턴 원칙 목록
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Architecture 조회 응답")
public record ArchitectureApiResponse(
        @Schema(description = "Architecture ID", example = "1") Long id,
        @Schema(description = "TechStack ID", example = "1") Long techStackId,
        @Schema(description = "아키텍처 이름", example = "Hexagonal Architecture") String name,
        @Schema(description = "패턴 타입", example = "HEXAGONAL") String patternType,
        @Schema(description = "패턴 설명", example = "포트와 어댑터 패턴을 사용한 헥사고날 아키텍처")
                String patternDescription,
        @Schema(
                        description = "패턴 원칙 목록",
                        example = "[\"Dependency Inversion\", \"Separation of Concerns\"]")
                List<String> patternPrinciples,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
