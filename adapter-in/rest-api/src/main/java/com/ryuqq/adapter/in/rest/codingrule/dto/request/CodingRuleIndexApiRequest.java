package com.ryuqq.adapter.in.rest.codingrule.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * CodingRuleIndexApiRequest - 코딩 규칙 인덱스 조회 요청 DTO
 *
 * <p>규칙 인덱스(code, name, severity, category)만 조회하기 위한 요청입니다.
 *
 * <p>RDTO-001: Request/Response DTO는 Record로 정의.
 *
 * @param conventionId 컨벤션 ID (null이면 전체)
 * @param severities 심각도 필터 목록 (BLOCKER, CRITICAL, MAJOR, MINOR)
 * @param categories 카테고리 필터 목록 (STRUCTURE, NAMING, DEPENDENCY, etc.)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "코딩 규칙 인덱스 조회 요청")
public record CodingRuleIndexApiRequest(
        @Schema(description = "컨벤션 ID (null이면 전체)", example = "1") Long conventionId,
        @Schema(
                        description = "심각도 필터 목록",
                        example = "[\"BLOCKER\", \"CRITICAL\"]",
                        allowableValues = {"BLOCKER", "CRITICAL", "MAJOR", "MINOR"})
                List<String> severities,
        @Schema(
                        description = "카테고리 필터 목록",
                        example = "[\"STRUCTURE\", \"NAMING\"]",
                        allowableValues = {
                            "STRUCTURE",
                            "NAMING",
                            "DEPENDENCY",
                            "TRANSACTION",
                            "EXCEPTION",
                            "MAPPING",
                            "VALIDATION",
                            "SECURITY",
                            "PERFORMANCE",
                            "TEST",
                            "DOCUMENTATION"
                        })
                List<String> categories) {

    /**
     * 전체 인덱스 조회 (필터 없음)
     *
     * @return CodingRuleIndexApiRequest
     */
    public static CodingRuleIndexApiRequest all() {
        return new CodingRuleIndexApiRequest(null, null, null);
    }
}
