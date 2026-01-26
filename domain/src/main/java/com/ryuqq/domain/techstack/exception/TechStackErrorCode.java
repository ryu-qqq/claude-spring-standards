package com.ryuqq.domain.techstack.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * TechStackErrorCode - 기술 스택 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum TechStackErrorCode implements ErrorCode {
    TECH_STACK_NOT_FOUND("TECH_STACK-001", 404, "TechStack not found"),
    TECH_STACK_DUPLICATE_NAME("TECH_STACK-002", 409, "TechStack name already exists"),
    TECH_STACK_HAS_CHILDREN("TECH_STACK-003", 409, "TechStack has child resources");

    private final String code;
    private final int httpStatus;
    private final String message;

    TechStackErrorCode(String code, int httpStatus, String message) {
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
