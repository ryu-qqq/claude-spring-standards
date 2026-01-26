package com.ryuqq.domain.module.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ModuleErrorCode - 모듈 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ModuleErrorCode implements ErrorCode {
    MODULE_NOT_FOUND("MODULE-001", 404, "Module not found"),
    MODULE_DUPLICATE_NAME("MODULE-002", 409, "Module name already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    ModuleErrorCode(String code, int httpStatus, String message) {
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
