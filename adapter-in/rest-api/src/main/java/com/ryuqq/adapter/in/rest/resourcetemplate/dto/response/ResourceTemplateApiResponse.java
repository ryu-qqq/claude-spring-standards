package com.ryuqq.adapter.in.rest.resourcetemplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ResourceTemplateApiResponse - ResourceTemplate API Response DTO
 *
 * <p>ResourceTemplate 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param resourceTemplateId 리소스 템플릿 ID
 * @param moduleId 모듈 ID
 * @param category 카테고리
 * @param filePath 파일 경로
 * @param fileType 파일 타입
 * @param description 설명 (nullable)
 * @param templateContent 템플릿 콘텐츠 (nullable)
 * @param required 필수 여부
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "리소스 템플릿 응답 DTO")
public record ResourceTemplateApiResponse(
        @Schema(description = "리소스 템플릿 ID", example = "1") Long resourceTemplateId,
        @Schema(description = "모듈 ID", example = "1") Long moduleId,
        @Schema(description = "카테고리", example = "CONFIG") String category,
        @Schema(description = "파일 경로", example = "src/main/resources/application.yml")
                String filePath,
        @Schema(description = "파일 타입", example = "YAML") String fileType,
        @Schema(description = "설명", example = "Spring Boot 메인 설정 파일", nullable = true)
                String description,
        @Schema(
                        description = "템플릿 콘텐츠",
                        example = "spring:\\n  profiles:\\n    active: local",
                        nullable = true)
                String templateContent,
        @Schema(description = "필수 여부", example = "true") Boolean required,
        @Schema(description = "생성 일시 (ISO 8601 형식)", example = "2024-01-15T10:30:00")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601 형식)", example = "2024-01-15T10:30:00")
                String updatedAt) {}
