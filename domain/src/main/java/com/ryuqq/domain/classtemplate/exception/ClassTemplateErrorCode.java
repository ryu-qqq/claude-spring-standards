package com.ryuqq.domain.classtemplate.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ClassTemplateErrorCode - 클래스 템플릿 에러 코드
 *
 * @author ryu-qqq
 */
public enum ClassTemplateErrorCode implements ErrorCode {
    CLASS_TEMPLATE_NOT_FOUND("CLASS_TEMPLATE-001", 404, "ClassTemplate not found"),
    CLASS_TEMPLATE_DUPLICATE_CODE(
            "CLASS_TEMPLATE-002", 409, "ClassTemplate with same code already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    ClassTemplateErrorCode(String code, int httpStatus, String message) {
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
