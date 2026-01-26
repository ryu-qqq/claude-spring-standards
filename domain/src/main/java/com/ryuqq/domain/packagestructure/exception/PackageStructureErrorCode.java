package com.ryuqq.domain.packagestructure.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * PackageStructureErrorCode - 패키지 구조 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum PackageStructureErrorCode implements ErrorCode {
    PACKAGE_STRUCTURE_NOT_FOUND("PACKAGE_STRUCTURE-001", 404, "PackageStructure not found"),
    DUPLICATE_PATH_PATTERN("PACKAGE_STRUCTURE-002", 409, "Duplicate path pattern in module");

    private final String code;
    private final int httpStatus;
    private final String message;

    PackageStructureErrorCode(String code, int httpStatus, String message) {
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
