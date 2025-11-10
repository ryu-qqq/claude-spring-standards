package com.ryuqq.domain.user.exception;

/**
 * 무효한 이메일 형식 예외.
 *
 * <p>RFC 5322 이메일 형식 위반 시 발생합니다.</p>
 *
 * <p>Domain Exception 설계:
 * <ul>
 *   <li>RuntimeException 상속 (Unchecked Exception)</li>
 *   <li>Domain Layer에서 비즈니스 규칙 위반 표현</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
public class InvalidEmailException extends RuntimeException {

    /**
     * 메시지와 함께 예외 생성.
     *
     * @param message 예외 메시지
     */
    public InvalidEmailException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인 예외와 함께 예외 생성.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
