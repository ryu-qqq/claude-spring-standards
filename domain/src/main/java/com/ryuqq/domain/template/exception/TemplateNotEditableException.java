package com.ryuqq.domain.template.exception;

import com.ryuqq.domain.template.vo.TemplateId;
import com.ryuqq.domain.template.vo.TemplateStatus;
import java.util.Map;

/** 편집 불가능한 상태에서 발생하는 예외. */
public final class TemplateNotEditableException extends TemplateException {

    public TemplateNotEditableException(TemplateId templateId, TemplateStatus currentStatus) {
        super(
                TemplateErrorCode.TEMPLATE_NOT_EDITABLE,
                String.format(
                        "Template %s is not editable in %s status",
                        templateId.asString(), currentStatus),
                Map.of(
                        "templateId", templateId.asString(),
                        "status", currentStatus.name()));
    }
}
