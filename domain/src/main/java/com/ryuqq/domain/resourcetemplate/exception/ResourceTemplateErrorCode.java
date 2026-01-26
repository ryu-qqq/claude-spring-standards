package com.ryuqq.domain.resourcetemplate.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ResourceTemplateErrorCode - 리소스 템플릿 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ResourceTemplateErrorCode implements ErrorCode {
    RESOURCE_TEMPLATE_NOT_FOUND("RESOURCE_TEMPLATE-001", 404, "ResourceTemplate not found");

    private final String code;
    private final int httpStatus;
    private final String message;

    ResourceTemplateErrorCode(String code, int httpStatus, String message) {
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
