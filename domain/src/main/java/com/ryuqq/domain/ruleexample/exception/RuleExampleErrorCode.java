package com.ryuqq.domain.ruleexample.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * RuleExampleErrorCode - 규칙 예시 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum RuleExampleErrorCode implements ErrorCode {
    RULE_EXAMPLE_NOT_FOUND("RULE_EXAMPLE-001", 404, "RuleExample not found");

    private final String code;
    private final int httpStatus;
    private final String message;

    RuleExampleErrorCode(String code, int httpStatus, String message) {
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
