package com.ryuqq.domain.template.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/** Template 도메인 예외 기본 클래스. */
public abstract class TemplateException extends DomainException {

    protected TemplateException(TemplateErrorCode errorCode) {
        super(errorCode);
    }

    protected TemplateException(TemplateErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected TemplateException(
            TemplateErrorCode errorCode, String message, Map<String, Object> args) {
        super(errorCode, message, args);
    }
}
