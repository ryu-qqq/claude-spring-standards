package com.ryuqq.domain.template.exception;

import com.ryuqq.domain.template.vo.TemplateId;
import com.ryuqq.domain.template.vo.TemplateSectionId;
import java.util.Map;

/** 섹션이 존재하지 않을 때 발생하는 예외. */
public final class TemplateSectionNotFoundException extends TemplateException {

    public TemplateSectionNotFoundException(TemplateId templateId, TemplateSectionId sectionId) {
        super(
                TemplateErrorCode.TEMPLATE_SECTION_NOT_FOUND,
                String.format(
                        "Template %s does not contain section %s",
                        templateId.asString(), sectionId.asString()),
                Map.of(
                        "templateId", templateId.asString(),
                        "sectionId", sectionId.asString()));
    }
}
