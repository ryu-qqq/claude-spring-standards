package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RuleExampleDetailApiResponse - 규칙 예시 상세 정보
 *
 * @param type 예시 타입 (GOOD/BAD)
 * @param code 예시 코드
 * @param explanation 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "규칙 예시 상세 정보")
public record RuleExampleDetailApiResponse(
        @Schema(description = "예시 타입", example = "GOOD") String type,
        @Schema(
                        description = "예시 코드",
                        example = "public class Order {\n    private final OrderId id;\n}")
                String code,
        @Schema(description = "설명", example = "수동으로 getter 구현") String explanation) {}
