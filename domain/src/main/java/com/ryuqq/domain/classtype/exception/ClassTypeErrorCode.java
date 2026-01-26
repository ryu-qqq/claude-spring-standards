package com.ryuqq.domain.classtype.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ClassTypeErrorCode - 클래스 타입 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ClassTypeErrorCode implements ErrorCode {
    CLASS_TYPE_NOT_FOUND("CLASS_TYPE-001", 404, "ClassType not found"),
    CLASS_TYPE_DUPLICATE_CODE("CLASS_TYPE-002", 409, "ClassType code already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    ClassTypeErrorCode(String code, int httpStatus, String message) {
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
