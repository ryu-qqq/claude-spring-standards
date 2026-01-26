package com.ryuqq.adapter.in.rest.packagepurpose.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PackagePurposeApiResponse - PackagePurpose API Response DTO
 *
 * <p>PackagePurpose 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param packagePurposeId 패키지 목적 ID
 * @param structureId 패키지 구조 ID
 * @param code 목적 코드
 * @param name 목적 이름
 * @param description 설명
 * @param defaultAllowedClassTypes 기본 허용 클래스 타입 목록
 * @param defaultNamingPattern 기본 네이밍 패턴
 * @param defaultNamingSuffix 기본 네이밍 접미사
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "PackagePurpose 응답")
public record PackagePurposeApiResponse(
        @Schema(description = "패키지 목적 ID", example = "1") Long packagePurposeId,
        @Schema(description = "패키지 구조 ID", example = "1") Long structureId,
        @Schema(description = "목적 코드", example = "AGGREGATE") String code,
        @Schema(description = "목적 이름", example = "Aggregate") String name,
        @Schema(description = "설명", example = "도메인 집합체 패키지", nullable = true) String description,
        @Schema(description = "기본 허용 클래스 타입 목록", example = "[\"CLASS\", \"RECORD\"]")
                List<String> defaultAllowedClassTypes,
        @Schema(description = "기본 네이밍 패턴", example = "^[A-Z][a-zA-Z0-9]*$", nullable = true)
                String defaultNamingPattern,
        @Schema(description = "기본 네이밍 접미사", example = "Aggregate", nullable = true)
                String defaultNamingSuffix,
        @Schema(description = "생성 일시 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
