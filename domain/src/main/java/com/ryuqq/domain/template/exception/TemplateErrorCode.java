package com.ryuqq.domain.template.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/** Template Bounded Context Error Codes. */
public enum TemplateErrorCode implements ErrorCode {
    TEMPLATE_NOT_FOUND("TEMPLATE-001", 404, "Template not found"),
    TEMPLATE_NOT_EDITABLE("TEMPLATE-002", 409, "Template is not editable in its current status"),
    TEMPLATE_SECTION_NOT_FOUND("TEMPLATE-003", 404, "Template section not found"),
    TEMPLATE_SECTION_LIMIT_EXCEEDED("TEMPLATE-004", 400, "Template section limit exceeded"),
    TEMPLATE_SECTION_ORDER_CONFLICT("TEMPLATE-005", 400, "Template section ordering conflict"),
    TEMPLATE_STATE_TRANSITION_NOT_ALLOWED(
            "TEMPLATE-006", 409, "Template state transition is not allowed"),
    TEMPLATE_SECTION_REQUIRED_FOR_PUBLICATION(
            "TEMPLATE-007", 400, "At least one section is required before publication"),
    TEMPLATE_TITLE_DUPLICATED("TEMPLATE-008", 400, "Template section title duplicated");

    private final String code;
    private final int httpStatus;
    private final String message;

    TemplateErrorCode(String code, int httpStatus, String message) {
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
