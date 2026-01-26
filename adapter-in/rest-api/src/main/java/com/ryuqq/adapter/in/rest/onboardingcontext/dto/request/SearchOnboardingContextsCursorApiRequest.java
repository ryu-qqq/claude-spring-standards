package com.ryuqq.adapter.in.rest.onboardingcontext.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * SearchOnboardingContextsCursorApiRequest - OnboardingContext 복합 조건 조회 API Request (커서 기반)
 *
 * <p>OnboardingContext 복합 조건 조회 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>기본값 처리는 Mapper에서 수행합니다. Request DTO에서는 기본값 설정 금지.
 *
 * @param cursor 커서 값 (마지막 항목의 ID, null이면 첫 페이지)
 * @param size 슬라이스 크기 (nullable, 최대: 100)
 * @param techStackIds TechStack ID 필터 목록
 * @param architectureIds Architecture ID 필터 목록
 * @param contextTypes 컨텍스트 타입 필터 (SUMMARY, ZERO_TOLERANCE, RULES_INDEX, MCP_USAGE)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "OnboardingContext 복합 조건 조회 요청 (커서 기반)")
public record SearchOnboardingContextsCursorApiRequest(
        @Parameter(description = "커서 값 (마지막 항목의 ID)", example = "123")
                @Schema(description = "커서 값 (마지막 항목의 ID)", nullable = true)
                String cursor,
        @Parameter(description = "슬라이스 크기", example = "20")
                @Schema(description = "슬라이스 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "슬라이스 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "슬라이스 크기는 100 이하여야 합니다")
                Integer size,
        @Parameter(description = "TechStack ID 필터 (복수 선택 가능)", example = "1,2,3")
                @Schema(description = "TechStack ID 필터 목록", nullable = true)
                List<Long> techStackIds,
        @Parameter(description = "Architecture ID 필터 (복수 선택 가능)", example = "1,2")
                @Schema(description = "Architecture ID 필터 목록", nullable = true)
                List<Long> architectureIds,
        @Parameter(description = "컨텍스트 타입 필터 (복수 선택 가능)", example = "SUMMARY,ZERO_TOLERANCE")
                @Schema(description = "컨텍스트 타입 필터 목록", nullable = true)
                List<String> contextTypes) {

    public SearchOnboardingContextsCursorApiRequest {
        techStackIds = techStackIds != null ? List.copyOf(techStackIds) : null;
        architectureIds = architectureIds != null ? List.copyOf(architectureIds) : null;
        contextTypes = contextTypes != null ? List.copyOf(contextTypes) : null;
    }
}
