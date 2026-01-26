package com.ryuqq.adapter.in.rest.ruleexample.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * CreateRuleExampleApiRequest - RuleExample 생성 API Request
 *
 * <p>RuleExample 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param ruleId 코딩 규칙 ID (필수)
 * @param exampleType 예시 타입 (GOOD, BAD)
 * @param code 예시 코드 (필수)
 * @param language 언어 (예: JAVA, KOTLIN)
 * @param explanation 설명
 * @param highlightLines 하이라이트 라인 목록 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "규칙 예시 생성 요청")
public record CreateRuleExampleApiRequest(
        @Schema(description = "코딩 규칙 ID", example = "10") @NotNull(message = "ruleId는 필수입니다")
                Long ruleId,
        @Schema(description = "예시 타입 (GOOD, BAD)", example = "GOOD")
                @NotBlank(message = "exampleType은 필수입니다")
                @Size(max = 20, message = "exampleType은 20자 이내여야 합니다")
                String exampleType,
        @Schema(description = "예시 코드", example = "public class OrderService {}")
                @NotBlank(message = "code는 필수입니다")
                @Size(max = 10000, message = "code는 10000자 이내여야 합니다")
                String code,
        @Schema(description = "언어", example = "JAVA")
                @NotBlank(message = "language는 필수입니다")
                @Size(max = 30, message = "language는 30자 이내여야 합니다")
                String language,
        @Schema(description = "설명", example = "올바른 서비스 클래스 정의 방법", nullable = true)
                @Size(max = 2000, message = "explanation은 2000자 이내여야 합니다")
                String explanation,
        @Schema(description = "하이라이트 라인 목록", example = "[1, 3, 5]", nullable = true)
                List<Integer> highlightLines) {}
