package com.ryuqq.domain.classtypecategory.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ClassTypeCategoryErrorCode - 클래스 타입 카테고리 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ClassTypeCategoryErrorCode implements ErrorCode {
    CLASS_TYPE_CATEGORY_NOT_FOUND("CLASS_TYPE_CATEGORY-001", 404, "ClassTypeCategory not found"),
    CLASS_TYPE_CATEGORY_DUPLICATE_CODE(
            "CLASS_TYPE_CATEGORY-002", 409, "ClassTypeCategory code already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    ClassTypeCategoryErrorCode(String code, int httpStatus, String message) {
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
