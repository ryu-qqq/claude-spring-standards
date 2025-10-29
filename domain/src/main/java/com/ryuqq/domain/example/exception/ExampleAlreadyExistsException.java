package com.ryuqq.domain.example.exception;

import com.ryuqq.domain.example.ExampleErrorCode;

import java.util.Map;

/**
 * Example이 이미 존재할 때 발생하는 예외
 *
 * <p>동일한 조건의 Example이 이미 존재하여 생성/수정할 수 없을 때 발생합니다.</p>
 *
 * <p><strong>HTTP 매핑:</strong> 409 Conflict</p>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * if (exampleRepository.existsByMessage(message)) {
 *     throw new ExampleAlreadyExistsException(message);
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public final class ExampleAlreadyExistsException extends ExampleException {

    /**
     * 메시지로 예외 생성 (중복된 메시지)
     *
     * @param message 중복된 Example의 메시지
     */
    public ExampleAlreadyExistsException(String message) {
        super(
            ExampleErrorCode.EXAMPLE_ALREADY_EXISTS.getCode(),
            ExampleErrorCode.EXAMPLE_ALREADY_EXISTS.getMessage(),
            Map.of("message", message)
        );
    }

    /**
     * ID로 예외 생성 (중복된 ID)
     *
     * @param id 중복된 Example의 ID
     */
    public ExampleAlreadyExistsException(Long id) {
        super(
            ExampleErrorCode.EXAMPLE_ALREADY_EXISTS.getCode(),
            ExampleErrorCode.EXAMPLE_ALREADY_EXISTS.getMessage(),
            Map.of("id", id)
        );
    }

    /**
     * 커스텀 메시지와 args로 예외 생성
     *
     * @param errorMessage 에러 메시지
     * @param args 추가 컨텍스트
     */
    public ExampleAlreadyExistsException(String errorMessage, Map<String, Object> args) {
        super(
            ExampleErrorCode.EXAMPLE_ALREADY_EXISTS.getCode(),
            errorMessage,
            args
        );
    }
}
