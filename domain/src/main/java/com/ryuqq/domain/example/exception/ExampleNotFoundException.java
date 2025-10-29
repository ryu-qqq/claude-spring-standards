package com.ryuqq.domain.example.exception;

import com.ryuqq.domain.example.ExampleErrorCode;

import java.util.Map;

/**
 * Example을 찾을 수 없을 때 발생하는 예외
 *
 * <p>지정된 ID나 조건으로 Example을 조회했지만 존재하지 않을 때 발생합니다.</p>
 *
 * <p><strong>HTTP 매핑:</strong> 404 Not Found</p>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * Example example = exampleRepository.findById(id)
 *     .orElseThrow(() -> new ExampleNotFoundException(id));
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public final class ExampleNotFoundException extends ExampleException {

    /**
     * Example ID로 예외 생성
     *
     * @param id 찾을 수 없는 Example의 ID
     */
    public ExampleNotFoundException(Long id) {
        super(
            ExampleErrorCode.EXAMPLE_NOT_FOUND.getCode(),
            ExampleErrorCode.EXAMPLE_NOT_FOUND.getMessage(),
            Map.of("id", id)
        );
    }

    /**
     * 커스텀 메시지로 예외 생성
     *
     * @param message 커스텀 에러 메시지
     */
    public ExampleNotFoundException(String message) {
        super(
            ExampleErrorCode.EXAMPLE_NOT_FOUND.getCode(),
            message
        );
    }

    /**
     * ID와 커스텀 메시지로 예외 생성
     *
     * @param id 찾을 수 없는 Example의 ID
     * @param message 커스텀 에러 메시지
     */
    public ExampleNotFoundException(Long id, String message) {
        super(
            ExampleErrorCode.EXAMPLE_NOT_FOUND.getCode(),
            message,
            Map.of("id", id)
        );
    }
}
