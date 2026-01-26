package com.ryuqq.adapter.in.rest.techstack.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * SearchTechStacksCursorApiRequest - TechStack 복합 조건 조회 API Request (커서 기반)
 *
 * <p>TechStack 복합 조건 조회 REST API 요청 DTO입니다. 상태 및 플랫폼 타입 필터링을 지원합니다.
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
 * @param status 상태 필터 (ACTIVE, DEPRECATED, ARCHIVED)
 * @param platformTypes 플랫폼 타입 필터 목록 (BACKEND, FRONTEND, FULLSTACK, SDK)
 * @author ryu-qqq
 */
@Schema(description = "TechStack 복합 조건 조회 요청 (커서 기반)")
public record SearchTechStacksCursorApiRequest(
        @Parameter(description = "커서 값 (마지막 항목의 ID)", example = "123")
                @Schema(description = "커서 값 (마지막 항목의 ID)", nullable = true)
                String cursor,
        @Parameter(description = "슬라이스 크기", example = "20")
                @Schema(description = "슬라이스 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "슬라이스 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "슬라이스 크기는 100 이하여야 합니다")
                Integer size,
        @Parameter(description = "상태 필터", example = "ACTIVE")
                @Schema(
                        description = "상태 필터 (ACTIVE, DEPRECATED, ARCHIVED)",
                        allowableValues = {"ACTIVE", "DEPRECATED", "ARCHIVED"},
                        nullable = true)
                String status,
        @Parameter(description = "플랫폼 타입 필터 (복수 선택 가능)", example = "BACKEND,FRONTEND")
                @Schema(
                        description = "플랫폼 타입 필터 목록 (BACKEND, FRONTEND, FULLSTACK, SDK)",
                        nullable = true)
                List<String> platformTypes) {}
