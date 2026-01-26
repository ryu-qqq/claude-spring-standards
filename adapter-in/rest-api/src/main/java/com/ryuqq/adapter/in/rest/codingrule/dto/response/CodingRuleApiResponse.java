package com.ryuqq.adapter.in.rest.codingrule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * CodingRuleApiResponse - CodingRule API Response DTO
 *
 * <p>CodingRule 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param codingRuleId 코딩 규칙 ID
 * @param conventionId 컨벤션 ID
 * @param structureId 패키지 구조 ID (nullable)
 * @param code 규칙 코드
 * @param name 규칙 이름
 * @param severity 심각도
 * @param category 카테고리
 * @param description 설명
 * @param rationale 근거
 * @param autoFixable 자동 수정 가능 여부
 * @param appliesTo 적용 대상 목록
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "CodingRule 조회 응답")
public record CodingRuleApiResponse(
        @Schema(description = "코딩 규칙 ID", example = "1") Long codingRuleId,
        @Schema(description = "컨벤션 ID", example = "1") Long conventionId,
        @Schema(description = "패키지 구조 ID", example = "1", nullable = true) Long structureId,
        @Schema(description = "규칙 코드", example = "AGG-001") String code,
        @Schema(description = "규칙 이름", example = "Lombok 사용 금지") String name,
        @Schema(description = "심각도", example = "BLOCKER") String severity,
        @Schema(description = "카테고리", example = "ANNOTATION") String category,
        @Schema(description = "설명", example = "도메인 레이어에서 Lombok 사용을 금지합니다") String description,
        @Schema(description = "근거", example = "Pure Java 원칙", nullable = true) String rationale,
        @Schema(description = "자동 수정 가능 여부", example = "false") boolean autoFixable,
        @Schema(description = "적용 대상 목록", example = "[\"AGGREGATE\", \"VALUE_OBJECT\"]")
                List<String> appliesTo,
        @Schema(description = "생성 일시 (ISO 8601 형식)", example = "2024-01-01T09:00:00+09:00")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601 형식)", example = "2024-01-01T09:00:00+09:00")
                String updatedAt) {}
