package com.ryuqq.adapter.in.rest.feedbackqueue.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateFeedbackApiRequest - FeedbackQueue 생성 API Request
 *
 * <p>FeedbackQueue 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param targetType 대상 타입 (CODING_RULE, CLASS_TEMPLATE, CONVENTION 등)
 * @param targetId 대상 ID
 * @param feedbackType 피드백 유형 (CREATE, UPDATE, DELETE)
 * @param payload 피드백 페이로드 (JSON 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "FeedbackQueue 생성 요청 DTO")
public record CreateFeedbackApiRequest(
        @Schema(description = "대상 타입", example = "CODING_RULE")
                @NotBlank(message = "targetType은 필수입니다")
                @Size(max = 50, message = "targetType은 50자 이내여야 합니다")
                String targetType,
        @Schema(description = "대상 ID", example = "100") @NotNull(message = "targetId는 필수입니다")
                Long targetId,
        @Schema(description = "피드백 유형", example = "UPDATE")
                @NotBlank(message = "feedbackType은 필수입니다")
                @Size(max = 50, message = "feedbackType은 50자 이내여야 합니다")
                String feedbackType,
        @Schema(description = "피드백 페이로드 (JSON 형식)", example = "{\"field\": \"value\"}")
                @NotBlank(message = "payload는 필수입니다")
                @Size(max = 10000, message = "payload는 10000자 이내여야 합니다")
                String payload) {}
