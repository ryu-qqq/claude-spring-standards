package com.ryuqq.domain.layer.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * LayerErrorCode - 레이어 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum LayerErrorCode implements ErrorCode {
    LAYER_NOT_FOUND("LAYER-001", 404, "Layer not found"),
    LAYER_DUPLICATE_CODE("LAYER-002", 409, "Layer code already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    LayerErrorCode(String code, int httpStatus, String message) {
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
