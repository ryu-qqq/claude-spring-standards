package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PackageStructureWithDetailsApiResponse - 상세 정보를 포함한 패키지 구조 응답
 *
 * @param id 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param description 설명
 * @param purposes 목적 목록
 * @param templates 템플릿 목록
 * @param archUnitTests ArchUnit 테스트 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상세 정보를 포함한 패키지 구조")
public record PackageStructureWithDetailsApiResponse(
        @Schema(description = "패키지 구조 ID", example = "10") Long id,
        @Schema(description = "경로 패턴", example = "com.{company}.domain.{domain}.aggregate")
                String pathPattern,
        @Schema(description = "설명", example = "Aggregate Root 패키지") String description,
        @Schema(description = "목적 목록") List<PackagePurposeDetailApiResponse> purposes,
        @Schema(description = "템플릿 목록") List<ClassTemplateDetailApiResponse> templates,
        @Schema(description = "ArchUnit 테스트 목록")
                List<ArchUnitTestDetailApiResponse> archUnitTests) {}
