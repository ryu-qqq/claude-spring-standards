package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PackageSummaryApiResponse - 패키지 요약 정보
 *
 * @param id 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param purposeSummary 목적 요약
 * @param allowedClassTypes 허용된 클래스 타입 목록
 * @param templateCount 템플릿 개수
 * @param ruleCount 규칙 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "패키지 요약 정보")
public record PackageSummaryApiResponse(
        @Schema(description = "패키지 구조 ID", example = "10") Long id,
        @Schema(description = "경로 패턴", example = "com.{company}.domain.{domain}.aggregate")
                String pathPattern,
        @Schema(description = "목적 요약", example = "Aggregate Root - 도메인 진입점") String purposeSummary,
        @Schema(
                        description = "허용된 클래스 타입 목록",
                        example = "[\"AGGREGATE\", \"ENTITY\", \"VALUE_OBJECT\"]")
                List<String> allowedClassTypes,
        @Schema(description = "템플릿 개수", example = "3") int templateCount,
        @Schema(description = "규칙 개수", example = "12") int ruleCount) {}
