package com.ryuqq.domain.zerotolerance.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ZeroToleranceRuleErrorCode - Zero Tolerance 규칙 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum ZeroToleranceRuleErrorCode implements ErrorCode {
    ZERO_TOLERANCE_RULE_NOT_FOUND("ZERO_TOLERANCE-001", 404, "ZeroToleranceRule not found"),
    ZERO_TOLERANCE_RULE_DUPLICATE(
            "ZERO_TOLERANCE-002",
            409,
            "ZeroToleranceRule already exists for this convention and coding rule"),
    ZERO_TOLERANCE_VIOLATION_DETECTED(
            "ZERO_TOLERANCE-003", 400, "Zero tolerance rule violation detected");

    private final String code;
    private final int httpStatus;
    private final String message;

    ZeroToleranceRuleErrorCode(String code, int httpStatus, String message) {
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
