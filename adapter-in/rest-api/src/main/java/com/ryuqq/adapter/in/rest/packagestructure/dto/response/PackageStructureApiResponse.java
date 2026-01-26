package com.ryuqq.adapter.in.rest.packagestructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PackageStructureApiResponse - PackageStructure API Response DTO
 *
 * <p>PackageStructure 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param packageStructureId 패키지 구조 ID
 * @param moduleId 모듈 ID
 * @param pathPattern 경로 패턴
 * @param allowedClassTypes 허용 클래스 타입 목록
 * @param namingPattern 네이밍 패턴 (nullable)
 * @param namingSuffix 네이밍 접미사 (nullable)
 * @param description 설명 (nullable)
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "PackageStructure 응답 DTO")
public record PackageStructureApiResponse(
        @Schema(description = "패키지 구조 ID", example = "1") Long packageStructureId,
        @Schema(description = "모듈 ID", example = "10") Long moduleId,
        @Schema(description = "경로 패턴", example = "com.ryuqq.domain.{aggregate}") String pathPattern,
        @Schema(description = "허용 클래스 타입 목록", example = "[\"AGGREGATE\", \"VALUE_OBJECT\"]")
                List<String> allowedClassTypes,
        @Schema(description = "네이밍 패턴", example = "PascalCase", nullable = true)
                String namingPattern,
        @Schema(description = "네이밍 접미사", example = "Aggregate", nullable = true)
                String namingSuffix,
        @Schema(description = "설명", example = "도메인 애그리게이트 패키지 구조", nullable = true)
                String description,
        @Schema(description = "생성 일시 (ISO 8601 형식)", example = "2025-01-23T10:30:00")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601 형식)", example = "2025-01-23T11:00:00")
                String updatedAt) {}
