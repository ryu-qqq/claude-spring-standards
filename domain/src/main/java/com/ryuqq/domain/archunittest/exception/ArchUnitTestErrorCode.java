package com.ryuqq.domain.archunittest.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ArchUnitTestErrorCode - ArchUnit 테스트 도메인 에러 코드
 *
 * <p>ArchUnit 테스트 도메인에서 발생할 수 있는 에러 코드를 정의합니다.
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public enum ArchUnitTestErrorCode implements ErrorCode {
    ARCH_UNIT_TEST_NOT_FOUND("AUT-001", 404, "ArchUnit 테스트를 찾을 수 없습니다"),
    ARCH_UNIT_TEST_ALREADY_EXISTS("AUT-002", 409, "이미 존재하는 ArchUnit 테스트입니다"),
    INVALID_TEST_CODE("AUT-003", 400, "유효하지 않은 테스트 코드입니다"),
    INVALID_TEST_TYPE("AUT-004", 400, "유효하지 않은 테스트 유형입니다"),
    DUPLICATE_TEST_NAME("AUT-005", 409, "중복된 테스트 이름입니다"),
    ARCH_UNIT_TEST_DUPLICATE_CODE("AUT-006", 409, "중복된 테스트 코드입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ArchUnitTestErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
