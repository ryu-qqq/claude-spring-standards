package com.ryuqq.domain.user.exception;

/**
 * 약한 패스워드 예외.
 *
 * <p>패스워드 강도 규칙 위반 시 발생합니다.</p>
 *
 * <p>비즈니스 규칙:
 * <ul>
 *   <li>최소 8자 이상</li>
 *   <li>영문 + 숫자 + 특수문자 조합</li>
 * </ul>
 * </p>
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
public class WeakPasswordException extends RuntimeException {

    /**
     * 메시지와 함께 예외 생성.
     *
     * @param message 예외 메시지
     */
    public WeakPasswordException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인 예외와 함께 예외 생성.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public WeakPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
