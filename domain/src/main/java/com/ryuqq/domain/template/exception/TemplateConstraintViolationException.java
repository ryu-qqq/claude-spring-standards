package com.ryuqq.domain.template.exception;

import java.util.Map;

/** Template 제약 위반 시 발생하는 예외. */
public class TemplateConstraintViolationException extends TemplateException {

    public TemplateConstraintViolationException(TemplateErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TemplateConstraintViolationException(
            TemplateErrorCode errorCode, String message, Map<String, Object> args) {
        super(errorCode, message, args);
    }
}
