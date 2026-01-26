package com.ryuqq.adapter.in.rest.feedbackqueue.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FeedbackQueueIdApiResponse - FeedbackQueue ID API Response
 *
 * <p>FeedbackQueue 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param feedbackQueueId 생성된 FeedbackQueue ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "FeedbackQueue ID 응답 DTO")
public record FeedbackQueueIdApiResponse(
        @Schema(description = "생성된 FeedbackQueue ID", example = "1") Long feedbackQueueId) {

    /**
     * 팩토리 메서드
     *
     * @param feedbackQueueId FeedbackQueue ID
     * @return FeedbackQueueIdApiResponse
     */
    public static FeedbackQueueIdApiResponse of(Long feedbackQueueId) {
        return new FeedbackQueueIdApiResponse(feedbackQueueId);
    }
}
