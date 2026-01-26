package com.ryuqq.domain.convention.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ConventionErrorCode - 컨벤션 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ConventionErrorCode implements ErrorCode {
    CONVENTION_NOT_FOUND("CONVENTION-001", 404, "Convention not found"),
    CONVENTION_DUPLICATE(
            "CONVENTION-002", 409, "Convention already exists for this layer and version");

    private final String code;
    private final int httpStatus;
    private final String message;

    ConventionErrorCode(String code, int httpStatus, String message) {
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
