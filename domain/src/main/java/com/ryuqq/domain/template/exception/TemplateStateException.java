package com.ryuqq.domain.template.exception;

import com.ryuqq.domain.template.vo.TemplateId;
import java.util.Map;

/** 허용되지 않은 상태 전이를 표현하는 예외. */
public final class TemplateStateException extends TemplateException {

    public TemplateStateException(
            TemplateErrorCode errorCode, String message, TemplateId templateId) {
        super(errorCode, message, Map.of("templateId", templateId.asString()));
    }
}
