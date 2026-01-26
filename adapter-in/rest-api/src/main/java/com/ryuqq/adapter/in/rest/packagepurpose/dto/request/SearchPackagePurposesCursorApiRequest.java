package com.ryuqq.adapter.in.rest.packagepurpose.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * SearchPackagePurposesCursorApiRequest - PackagePurpose 복합 조건 조회 API Request (커서 기반)
 *
 * <p>PackagePurpose 목록을 커서 기반으로 조회하는 REST API 요청 DTO입니다.
 *
 * <p>구조 ID(복수) 필터링과 검색(필드/키워드)을 지원합니다.
 *
 * @param cursor 커서 값 (마지막 항목의 ID, null이면 첫 페이지)
 * @param size 슬라이스 크기 (nullable, 최대: 100)
 * @param structureIds 패키지 구조 ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION)
 * @param searchWord 검색어 (searchField와 함께 사용)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "PackagePurpose 복합 조건 조회 요청 (커서 기반)")
public record SearchPackagePurposesCursorApiRequest(
        @Parameter(description = "커서 값 (마지막 항목의 ID)", example = "123")
                @Schema(description = "커서 값 (마지막 항목의 ID)", nullable = true)
                String cursor,
        @Parameter(description = "슬라이스 크기", example = "20")
                @Schema(description = "슬라이스 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "슬라이스 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "슬라이스 크기는 100 이하여야 합니다")
                Integer size,
        @Parameter(description = "패키지 구조 ID 필터 (복수 선택 가능)", example = "1,2,3")
                @Schema(description = "패키지 구조 ID 필터 목록", nullable = true)
                List<Long> structureIds,
        @Parameter(description = "검색 필드", example = "CODE")
                @Schema(description = "검색 필드 (CODE, NAME, DESCRIPTION)", nullable = true)
                String searchField,
        @Parameter(description = "검색어", example = "AGGREGATE")
                @Schema(description = "검색어", nullable = true)
                String searchWord) {}
