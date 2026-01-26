package com.ryuqq.adapter.in.rest.codingrule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CodingRuleIdApiResponse - CodingRule ID API Response
 *
 * <p>CodingRule 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param codingRuleId 생성된 CodingRule ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "CodingRule 생성 응답")
public record CodingRuleIdApiResponse(
        @Schema(description = "생성된 코딩 규칙 ID", example = "1") Long codingRuleId) {

    /**
     * 팩토리 메서드
     *
     * @param codingRuleId CodingRule ID
     * @return CodingRuleIdApiResponse
     */
    public static CodingRuleIdApiResponse of(Long codingRuleId) {
        return new CodingRuleIdApiResponse(codingRuleId);
    }
}
