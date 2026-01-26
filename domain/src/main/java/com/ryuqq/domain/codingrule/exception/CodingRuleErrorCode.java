package com.ryuqq.domain.codingrule.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * CodingRuleErrorCode - 코딩 규칙 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum CodingRuleErrorCode implements ErrorCode {
    CODING_RULE_NOT_FOUND("CODING_RULE-001", 404, "CodingRule not found"),
    CODING_RULE_DUPLICATE_CODE("CODING_RULE-002", 409, "CodingRule code already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    CodingRuleErrorCode(String code, int httpStatus, String message) {
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
