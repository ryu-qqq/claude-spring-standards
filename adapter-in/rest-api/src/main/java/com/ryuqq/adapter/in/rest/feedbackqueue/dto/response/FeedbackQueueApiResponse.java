package com.ryuqq.adapter.in.rest.feedbackqueue.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FeedbackQueueApiResponse - FeedbackQueue API Response DTO
 *
 * <p>FeedbackQueue 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param feedbackQueueId 피드백 큐 ID
 * @param targetType 대상 타입
 * @param targetId 대상 ID (nullable)
 * @param feedbackType 피드백 유형
 * @param riskLevel 리스크 레벨
 * @param payload 피드백 페이로드 (JSON)
 * @param status 현재 상태
 * @param reviewNotes 리뷰 노트 (거절 사유 등, nullable)
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "FeedbackQueue 응답 DTO")
public record FeedbackQueueApiResponse(
        @Schema(description = "피드백 큐 ID", example = "1") Long feedbackQueueId,
        @Schema(description = "대상 타입", example = "CODING_RULE") String targetType,
        @Schema(description = "대상 ID", example = "100", nullable = true) Long targetId,
        @Schema(description = "피드백 유형", example = "UPDATE") String feedbackType,
        @Schema(description = "리스크 레벨", example = "LOW") String riskLevel,
        @Schema(description = "피드백 페이로드 (JSON)", example = "{\"field\": \"value\"}") String payload,
        @Schema(description = "현재 상태", example = "PENDING") String status,
        @Schema(description = "리뷰 노트", example = "검토 필요", nullable = true) String reviewNotes,
        @Schema(description = "생성 일시 (ISO 8601 형식)", example = "2025-01-23T10:30:00")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601 형식)", example = "2025-01-23T11:00:00")
                String updatedAt) {}
