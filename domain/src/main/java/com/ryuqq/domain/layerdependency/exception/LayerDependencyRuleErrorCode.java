package com.ryuqq.domain.layerdependency.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * LayerDependencyRuleErrorCode - 레이어 의존성 규칙 도메인 에러 코드
 *
 * @author ryu-qqq
 */
public enum LayerDependencyRuleErrorCode implements ErrorCode {
    LAYER_DEPENDENCY_RULE_NOT_FOUND("LAYER_DEPENDENCY-001", 404, "LayerDependencyRule not found"),
    LAYER_DEPENDENCY_RULE_DUPLICATE(
            "LAYER_DEPENDENCY-002", 409, "LayerDependencyRule already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    LayerDependencyRuleErrorCode(String code, int httpStatus, String message) {
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
