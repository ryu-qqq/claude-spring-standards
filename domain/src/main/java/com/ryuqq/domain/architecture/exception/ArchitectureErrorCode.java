package com.ryuqq.domain.architecture.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ArchitectureErrorCode - 아키텍처 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ArchitectureErrorCode implements ErrorCode {
    ARCHITECTURE_NOT_FOUND("ARCHITECTURE-001", 404, "Architecture not found"),
    ARCHITECTURE_DUPLICATE_NAME("ARCHITECTURE-002", 409, "Architecture name already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    ArchitectureErrorCode(String code, int httpStatus, String message) {
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
