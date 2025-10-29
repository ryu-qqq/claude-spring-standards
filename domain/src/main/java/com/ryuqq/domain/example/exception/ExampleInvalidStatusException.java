package com.ryuqq.domain.example.exception;

import com.ryuqq.domain.example.ExampleErrorCode;

import java.util.Map;

/**
 * Example의 상태가 유효하지 않을 때 발생하는 예외
 *
 * <p>Example의 상태 전환이 불가능하거나, 현재 상태에서 수행할 수 없는 작업을 시도할 때 발생합니다.</p>
 *
 * <p><strong>HTTP 매핑:</strong> 400 Bad Request</p>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // 잘못된 상태 전환 시도
 * if (!example.canTransitionTo(newStatus)) {
 *     throw new ExampleInvalidStatusException(
 *         example.getStatus(),
 *         newStatus
 *     );
 * }
 *
 * // 현재 상태에서 작업 불가
 * if (example.isDeleted()) {
 *     throw new ExampleInvalidStatusException(
 *         "삭제된 Example은 수정할 수 없습니다",
 *         Map.of("currentStatus", example.getStatus())
 *     );
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public final class ExampleInvalidStatusException extends ExampleException {

    /**
     * 현재 상태와 시도한 상태로 예외 생성
     *
     * @param currentStatus 현재 Example의 상태
     * @param attemptedStatus 시도한 상태
     */
    public ExampleInvalidStatusException(String currentStatus, String attemptedStatus) {
        super(
            ExampleErrorCode.EXAMPLE_INVALID_STATUS.getCode(),
            ExampleErrorCode.EXAMPLE_INVALID_STATUS.getMessage(),
            Map.of(
                "currentStatus", currentStatus,
                "attemptedStatus", attemptedStatus
            )
        );
    }

    /**
     * 현재 상태만으로 예외 생성
     *
     * @param currentStatus 현재 Example의 상태
     */
    public ExampleInvalidStatusException(String currentStatus) {
        super(
            ExampleErrorCode.EXAMPLE_INVALID_STATUS.getCode(),
            ExampleErrorCode.EXAMPLE_INVALID_STATUS.getMessage(),
            Map.of("currentStatus", currentStatus)
        );
    }

    /**
     * 커스텀 메시지와 args로 예외 생성
     *
     * @param message 커스텀 에러 메시지
     * @param args 추가 컨텍스트
     */
    public ExampleInvalidStatusException(String message, Map<String, Object> args) {
        super(
            ExampleErrorCode.EXAMPLE_INVALID_STATUS.getCode(),
            message,
            args
        );
    }
}
