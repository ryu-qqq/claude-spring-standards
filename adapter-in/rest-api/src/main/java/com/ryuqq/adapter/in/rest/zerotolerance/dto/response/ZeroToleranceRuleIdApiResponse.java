package com.ryuqq.adapter.in.rest.zerotolerance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ZeroToleranceRuleIdApiResponse - Zero-Tolerance 규칙 ID API Response
 *
 * <p>Zero-Tolerance 규칙 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param zeroToleranceRuleId 생성된 Zero-Tolerance 규칙 ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Zero-Tolerance 규칙 생성 응답")
public record ZeroToleranceRuleIdApiResponse(
        @Schema(description = "생성된 Zero-Tolerance 규칙 ID", example = "1") Long zeroToleranceRuleId) {

    /**
     * 팩토리 메서드
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @return ZeroToleranceRuleIdApiResponse
     */
    public static ZeroToleranceRuleIdApiResponse of(Long zeroToleranceRuleId) {
        return new ZeroToleranceRuleIdApiResponse(zeroToleranceRuleId);
    }
}
