package com.ryuqq.domain.classtemplate.vo;

/**
 * TemplateDescription - 템플릿 설명
 *
 * @author ryu-qqq
 */
public record TemplateDescription(String value) {

    public TemplateDescription {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TemplateDescription must not be blank");
        }
    }

    public static TemplateDescription of(String value) {
        return new TemplateDescription(value);
    }
}
