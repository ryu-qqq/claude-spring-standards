package com.ryuqq.adapter.in.rest.zerotolerance.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * SearchZeroToleranceRulesCursorApiRequest - ZeroToleranceRule 복합 조건 조회 API Request (커서 기반)
 *
 * <p>ZeroToleranceRule 복합 조건 조회 REST API 요청 DTO입니다. 컨벤션 ID(복수), 탐지 방식(복수), 검색(필드/키워드), PR 자동 거부 여부
 * 필터링을 지원합니다.
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
 * @param conventionIds 컨벤션 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param detectionTypes 탐지 방식 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (TYPE)
 * @param searchWord 검색어
 * @param autoRejectPr PR 자동 거부 여부 필터
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ZeroToleranceRule 복합 조건 조회 요청 (커서 기반)")
public record SearchZeroToleranceRulesCursorApiRequest(
        @Parameter(description = "커서 값 (마지막 항목의 ID)", example = "123")
                @Schema(description = "커서 값 (마지막 항목의 ID)", nullable = true)
                String cursor,
        @Parameter(description = "슬라이스 크기", example = "20")
                @Schema(description = "슬라이스 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "슬라이스 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "슬라이스 크기는 100 이하여야 합니다")
                Integer size,
        @Parameter(description = "컨벤션 ID 필터 (복수 선택 가능)", example = "1,2,3")
                @Schema(description = "컨벤션 ID 필터 목록", nullable = true)
                List<Long> conventionIds,
        @Parameter(description = "탐지 방식 필터 (복수 선택 가능)", example = "REGEX,AST")
                @Schema(description = "탐지 방식 필터 목록", nullable = true)
                List<String> detectionTypes,
        @Parameter(description = "검색 필드", example = "TYPE")
                @Schema(description = "검색 필드 (TYPE)", nullable = true)
                String searchField,
        @Parameter(description = "검색어", example = "LOMBOK_IN_DOMAIN")
                @Schema(description = "검색어", nullable = true)
                String searchWord,
        @Parameter(description = "PR 자동 거부 여부 필터", example = "true")
                @Schema(description = "PR 자동 거부 여부 필터", nullable = true)
                Boolean autoRejectPr) {}
