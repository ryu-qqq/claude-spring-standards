package com.ryuqq.adapter.in.rest.zerotolerance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ZeroToleranceRuleSliceApiResponse - ZeroToleranceRule 슬라이스 조회 API Response DTO
 *
 * <p>커서 기반 페이징으로 조회된 Zero-Tolerance 규칙 상세 목록을 담습니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * @param rules Zero-Tolerance 규칙 상세 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursorId 다음 페이지 커서 ID (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Zero-Tolerance 규칙 슬라이스 조회 응답")
public record ZeroToleranceRuleSliceApiResponse(
        @Schema(description = "Zero-Tolerance 규칙 상세 목록")
                List<ZeroToleranceRuleDetailApiResponse> rules,
        @Schema(description = "다음 페이지 존재 여부", example = "true") boolean hasNext,
        @Schema(description = "다음 페이지 커서 ID", nullable = true, example = "100") Long nextCursorId) {

    /** Compact Constructor - Defensive Copy */
    public ZeroToleranceRuleSliceApiResponse {
        rules = List.copyOf(rules);
    }
}
