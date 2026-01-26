package com.ryuqq.domain.zerotolerance.vo;

/**
 * ErrorMessage - 에러 메시지 Value Object
 *
 * <p>Zero Tolerance 규칙 위반 시 표시되는 에러 메시지입니다.
 *
 * @author ryu-qqq
 */
public record ErrorMessage(String value) {

    private static final int MAX_LENGTH = 500;

    /** Compact Constructor - 유효성 검증 */
    public ErrorMessage {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ErrorMessage must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ErrorMessage must not exceed " + MAX_LENGTH + " characters");
        }
    }

    /** 정적 팩토리 메서드 */
    public static ErrorMessage of(String value) {
        return new ErrorMessage(value);
    }

    /**
     * 포맷된 에러 메시지 생성
     *
     * @param args 포맷 인자
     * @return 포맷된 에러 메시지
     */
    public String format(Object... args) {
        return String.format(value, args);
    }
}
