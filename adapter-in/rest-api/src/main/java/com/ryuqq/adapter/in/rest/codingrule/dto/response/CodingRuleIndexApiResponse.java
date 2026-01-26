package com.ryuqq.adapter.in.rest.codingrule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CodingRuleIndexApiResponse - 코딩 규칙 인덱스 응답 DTO
 *
 * <p>규칙의 핵심 정보만 포함하여 캐싱 효율성을 높입니다.
 *
 * <p>상세 정보는 get_rule(code) API로 개별 조회합니다.
 *
 * <p>RDTO-001: Request/Response DTO는 Record로 정의.
 *
 * @param code 규칙 코드 (예: DOM-AGG-001)
 * @param name 규칙 이름
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR)
 * @param category 카테고리 (STRUCTURE, NAMING, DEPENDENCY, etc.)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "코딩 규칙 인덱스 응답")
public record CodingRuleIndexApiResponse(
        @Schema(description = "규칙 코드", example = "DOM-AGG-001") String code,
        @Schema(description = "규칙 이름", example = "Aggregate에 Lombok 금지") String name,
        @Schema(description = "심각도", example = "BLOCKER") String severity,
        @Schema(description = "카테고리", example = "STRUCTURE") String category) {

    /**
     * CodingRuleIndexApiResponse 생성
     *
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @return CodingRuleIndexApiResponse
     */
    public static CodingRuleIndexApiResponse of(
            String code, String name, String severity, String category) {
        return new CodingRuleIndexApiResponse(code, name, severity, category);
    }
}
