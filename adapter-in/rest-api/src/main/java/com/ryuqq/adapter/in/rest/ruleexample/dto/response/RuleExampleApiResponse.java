package com.ryuqq.adapter.in.rest.ruleexample.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * RuleExampleApiResponse - RuleExample API Response DTO
 *
 * <p>RuleExample 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param ruleExampleId 규칙 예시 ID
 * @param ruleId 코딩 규칙 ID
 * @param exampleType 예시 타입 (GOOD, BAD)
 * @param code 예시 코드
 * @param language 언어
 * @param explanation 설명 (nullable)
 * @param highlightLines 하이라이트 라인 목록
 * @param source 예시 소스 (MANUAL, AGENT_FEEDBACK)
 * @param feedbackId 피드백 ID (nullable)
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "규칙 예시 응답")
public record RuleExampleApiResponse(
        @Schema(description = "규칙 예시 ID", example = "1") Long ruleExampleId,
        @Schema(description = "코딩 규칙 ID", example = "10") Long ruleId,
        @Schema(description = "예시 타입", example = "GOOD") String exampleType,
        @Schema(description = "예시 코드", example = "public class OrderService {}") String code,
        @Schema(description = "언어", example = "JAVA") String language,
        @Schema(description = "설명", example = "올바른 서비스 클래스 정의 방법", nullable = true)
                String explanation,
        @Schema(description = "하이라이트 라인 목록", example = "[1, 3, 5]") List<Integer> highlightLines,
        @Schema(description = "예시 소스", example = "MANUAL") String source,
        @Schema(description = "피드백 ID", example = "100", nullable = true) Long feedbackId,
        @Schema(description = "생성 일시", example = "2024-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정 일시", example = "2024-01-15T10:30:00Z") String updatedAt) {}
