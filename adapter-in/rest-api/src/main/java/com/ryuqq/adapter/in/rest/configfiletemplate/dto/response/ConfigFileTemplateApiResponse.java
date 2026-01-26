package com.ryuqq.adapter.in.rest.configfiletemplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ConfigFileTemplateApiResponse - ConfigFileTemplate 조회 API Response
 *
 * <p>ConfigFileTemplate 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param id ConfigFileTemplate ID
 * @param techStackId TechStack ID (FK)
 * @param architectureId Architecture ID (FK, nullable)
 * @param toolType 도구 타입 (CLAUDE, CURSOR, COPILOT 등)
 * @param filePath 파일 경로
 * @param fileName 파일명
 * @param content 파일 내용
 * @param category 카테고리
 * @param description 템플릿 설명
 * @param variables 치환 가능한 변수 정의 (JSON)
 * @param displayOrder 정렬 순서
 * @param isRequired 필수 파일 여부
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ConfigFileTemplate 조회 응답")
public record ConfigFileTemplateApiResponse(
        @Schema(description = "ConfigFileTemplate ID", example = "1") Long id,
        @Schema(description = "TechStack ID", example = "1") Long techStackId,
        @Schema(description = "Architecture ID", example = "1", nullable = true)
                Long architectureId,
        @Schema(description = "도구 타입", example = "CLAUDE") String toolType,
        @Schema(description = "파일 경로", example = ".claude/CLAUDE.md") String filePath,
        @Schema(description = "파일명", example = "CLAUDE.md") String fileName,
        @Schema(description = "파일 내용", example = "# Project Config...") String content,
        @Schema(description = "카테고리", example = "MAIN_CONFIG", nullable = true) String category,
        @Schema(description = "템플릿 설명", example = "Claude 메인 설정 파일", nullable = true)
                String description,
        @Schema(
                        description = "치환 가능한 변수 정의 (JSON)",
                        example = "{\"project_name\": \"string\"}",
                        nullable = true)
                String variables,
        @Schema(description = "정렬 순서", example = "0", nullable = true) Integer displayOrder,
        @Schema(description = "필수 파일 여부", example = "true") Boolean isRequired,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
