package com.ryuqq.domain.configfiletemplate.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ConfigFileTemplateErrorCode - 설정 파일 템플릿 도메인 에러 코드
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public enum ConfigFileTemplateErrorCode implements ErrorCode {
    CONFIG_FILE_TEMPLATE_NOT_FOUND(
            "CONFIG_FILE_TEMPLATE-001", 404, "Config file template not found");

    private final String code;
    private final int httpStatus;
    private final String message;

    ConfigFileTemplateErrorCode(String code, int httpStatus, String message) {
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
