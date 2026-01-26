package com.ryuqq.domain.packagepurpose.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * PackagePurposeErrorCode - 패키지 목적 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum PackagePurposeErrorCode implements ErrorCode {
    PACKAGE_PURPOSE_NOT_FOUND("PACKAGE_PURPOSE-001", 404, "PackagePurpose not found"),
    PACKAGE_PURPOSE_DUPLICATE_CODE(
            "PACKAGE_PURPOSE-002", 409, "PackagePurpose code already exists in this module type");

    private final String code;
    private final int httpStatus;
    private final String message;

    PackagePurposeErrorCode(String code, int httpStatus, String message) {
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
