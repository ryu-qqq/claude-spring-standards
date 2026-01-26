package com.ryuqq.adapter.in.rest.ruleexample.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RuleExampleIdApiResponse - RuleExample ID API Response
 *
 * <p>RuleExample 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param ruleExampleId 생성된 RuleExample ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "규칙 예시 ID 응답")
public record RuleExampleIdApiResponse(
        @Schema(description = "생성된 규칙 예시 ID", example = "1") Long ruleExampleId) {

    /**
     * 팩토리 메서드
     *
     * @param ruleExampleId RuleExample ID
     * @return RuleExampleIdApiResponse
     */
    public static RuleExampleIdApiResponse of(Long ruleExampleId) {
        return new RuleExampleIdApiResponse(ruleExampleId);
    }
}
