package com.ryuqq.domain.onboardingcontext.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * OnboardingContextErrorCode - 온보딩 컨텍스트 도메인 에러 코드
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public enum OnboardingContextErrorCode implements ErrorCode {
    ONBOARDING_CONTEXT_NOT_FOUND("ONBOARDING_CONTEXT-001", 404, "Onboarding context not found");

    private final String code;
    private final int httpStatus;
    private final String message;

    OnboardingContextErrorCode(String code, int httpStatus, String message) {
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
