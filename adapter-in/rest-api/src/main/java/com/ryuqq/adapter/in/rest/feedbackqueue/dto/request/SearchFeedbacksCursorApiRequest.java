package com.ryuqq.adapter.in.rest.feedbackqueue.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * SearchFeedbacksCursorApiRequest - FeedbackQueue 복합 조건 조회 API Request (커서 기반)
 *
 * <p>FeedbackQueue 목록을 커서 기반으로 조회합니다. 상태/대상 타입/피드백 타입/리스크/액션 필터(복수)를 지원합니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>CUR-001: 커서 기반 조회 패턴 권장.
 *
 * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
 *
 * @param cursor 커서 값 (마지막 항목의 ID, null이면 첫 페이지)
 * @param size 슬라이스 크기 (기본값: 20, 최대: 100)
 * @param statuses 상태 필터 (복수 선택 가능)
 * @param targetTypes 대상 타입 필터 (복수 선택 가능)
 * @param feedbackTypes 피드백 타입 필터 (복수 선택 가능)
 * @param riskLevels 리스크 레벨 필터 (복수 선택 가능)
 * @param actions 처리 액션 필터 (복수 선택 가능)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "FeedbackQueue 복합 조건 조회 요청 (커서 기반)")
public record SearchFeedbacksCursorApiRequest(
        @Parameter(description = "커서 값 (마지막 항목의 ID)", example = "123")
                @Schema(description = "커서 값 (마지막 항목의 ID)", nullable = true)
                String cursor,
        @Parameter(description = "슬라이스 크기", example = "20")
                @Schema(description = "슬라이스 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "슬라이스 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "슬라이스 크기는 100 이하여야 합니다")
                Integer size,
        @Parameter(description = "상태 필터 (복수 선택 가능)", example = "PENDING,LLM_APPROVED")
                @Schema(description = "상태 필터 목록", nullable = true)
                List<String> statuses,
        @Parameter(description = "대상 타입 필터 (복수 선택 가능)", example = "CODING_RULE,RULE_EXAMPLE")
                @Schema(description = "대상 타입 필터 목록", nullable = true)
                List<String> targetTypes,
        @Parameter(description = "피드백 타입 필터 (복수 선택 가능)", example = "ADD,MODIFY")
                @Schema(description = "피드백 타입 필터 목록", nullable = true)
                List<String> feedbackTypes,
        @Parameter(description = "리스크 레벨 필터 (복수 선택 가능)", example = "SAFE,MEDIUM")
                @Schema(description = "리스크 레벨 필터 목록", nullable = true)
                List<String> riskLevels,
        @Parameter(description = "처리 액션 필터 (복수 선택 가능)", example = "LLM_APPROVE,HUMAN_REJECT")
                @Schema(description = "처리 액션 필터 목록", nullable = true)
                List<String> actions) {}
