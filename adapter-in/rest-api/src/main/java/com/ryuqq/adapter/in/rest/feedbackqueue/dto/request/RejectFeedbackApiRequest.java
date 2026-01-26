package com.ryuqq.adapter.in.rest.feedbackqueue.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * RejectFeedbackApiRequest - FeedbackQueue 거절 API Request
 *
 * <p>FeedbackQueue 거절 시 사용하는 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param reviewNotes 거절 사유 (nullable, 최대 2000자)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "FeedbackQueue 거절 요청 DTO")
public record RejectFeedbackApiRequest(
        @Schema(description = "거절 사유", example = "규칙 위반으로 인해 거절합니다.", nullable = true)
                @Size(max = 2000, message = "reviewNotes는 2000자 이내여야 합니다")
                String reviewNotes) {}
