package com.ryuqq.adapter.in.rest.codingrule.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * SearchCodingRulesCursorApiRequest - CodingRule 복합 조건 조회 API Request (커서 기반)
 *
 * <p>CodingRule 복합 조건 조회 REST API 요청 DTO입니다. 카테고리, 심각도 필터링 및 필드별 검색을 지원합니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>기본값 처리는 Mapper에서 수행합니다. Request DTO에서는 기본값 설정 금지.
 *
 * <p>커서 기반 조회: cursor는 마지막 항목의 ID이며, DESC 정렬로 조회합니다.
 *
 * @param cursor 커서 값 (마지막 항목의 ID, null이면 첫 페이지)
 * @param size 슬라이스 크기 (nullable, 최대: 100)
 * @param categories 카테고리 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param severities 심각도 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION, null이면 검색 안 함)
 * @param searchWord 검색어 (부분 일치, 최대 255자, null이면 검색 안 함)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "CodingRule 복합 조건 조회 요청 (커서 기반)")
public record SearchCodingRulesCursorApiRequest(
        @Parameter(description = "커서 값 (마지막 항목의 ID)", example = "123")
                @Schema(description = "커서 값 (마지막 항목의 ID)", nullable = true)
                String cursor,
        @Parameter(description = "슬라이스 크기", example = "20")
                @Schema(description = "슬라이스 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "슬라이스 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "슬라이스 크기는 100 이하여야 합니다")
                Integer size,
        @Parameter(description = "카테고리 필터 (복수 선택 가능)", example = "ANNOTATION,BEHAVIOR")
                @Schema(description = "카테고리 필터 목록", nullable = true)
                List<String> categories,
        @Parameter(description = "심각도 필터 (복수 선택 가능)", example = "BLOCKER,CRITICAL")
                @Schema(description = "심각도 필터 목록", nullable = true)
                List<String> severities,
        @Parameter(description = "검색 필드 (CODE, NAME, DESCRIPTION)", example = "CODE")
                @Schema(
                        description = "검색 필드",
                        allowableValues = {"CODE", "NAME", "DESCRIPTION"},
                        nullable = true)
                String searchField,
        @Parameter(description = "검색어 (부분 일치)", example = "CTR-001")
                @Schema(description = "검색어 (부분 일치, 최대 255자)", nullable = true, maxLength = 255)
                @Size(max = 255, message = "검색어는 255자 이하여야 합니다")
                String searchWord) {}
