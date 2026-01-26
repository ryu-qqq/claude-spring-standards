package com.ryuqq.adapter.in.rest.common.dto;

import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import org.slf4j.MDC;

/**
 * ApiResponse - 표준 API 성공 응답 래퍼
 *
 * <p>모든 REST API 성공 응답의 일관된 형식을 제공합니다.
 *
 * <p><strong>응답 형식:</strong>
 *
 * <pre>{@code
 * {
 *   "data": { ... },
 *   "timestamp": "2025-12-22T10:30:00+09:00",
 *   "requestId": "550e8400-e29b-41d4-a716-446655440000"
 * }
 * }</pre>
 *
 * <p><strong>에러 응답:</strong>
 *
 * <p>에러 응답은 RFC 7807 ProblemDetail을 사용합니다. GlobalExceptionHandler에서 처리됩니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // 데이터 응답
 * ApiResponse<UserDto> response = ApiResponse.of(userDto);
 *
 * // 빈 응답 (생성/삭제 등)
 * ApiResponse<Void> response = ApiResponse.of();
 * }</pre>
 *
 * @param <T> 응답 데이터 타입
 * @param data 응답 데이터 (nullable)
 * @param timestamp 응답 시간 (ISO 8601 형식, 타임존 포함)
 * @param requestId 요청 ID (traceId 또는 UUID)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "표준 API 성공 응답")
public record ApiResponse<T>(
        @Schema(description = "응답 데이터", nullable = true) T data,
        @Schema(description = "응답 시간 (ISO 8601 형식)", example = "2025-01-23T10:30:00+09:00")
                String timestamp,
        @Schema(
                        description = "요청 ID (traceId 또는 UUID)",
                        example = "550e8400-e29b-41d4-a716-446655440000")
                String requestId) {

    /**
     * 성공 응답 생성
     *
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, DateTimeFormatUtils.nowIso8601(), generateRequestId());
    }

    /**
     * 빈 성공 응답 생성 (데이터 없음)
     *
     * @param <T> 데이터 타입
     * @return ApiResponse (data = null)
     */
    public static <T> ApiResponse<T> of() {
        return of(null);
    }

    /**
     * 성공 응답 생성 (of 메서드의 별칭)
     *
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return of(data);
    }

    /**
     * Request ID 생성
     *
     * <p>MDC의 traceId를 우선 사용하고, 없으면 UUID를 생성합니다.
     *
     * <p>분산 추적 시스템 (Micrometer, Sleuth 등)과 통합됩니다.
     *
     * @return Request ID (traceId 또는 UUID)
     */
    private static String generateRequestId() {
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        return UUID.randomUUID().toString();
    }
}
