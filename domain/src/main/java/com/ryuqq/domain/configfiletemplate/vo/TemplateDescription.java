package com.ryuqq.domain.configfiletemplate.vo;

/**
 * TemplateDescription - 템플릿 설명 Value Object
 *
 * <p>템플릿에 대한 설명입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record TemplateDescription(String value) {

    public static TemplateDescription of(String value) {
        return new TemplateDescription(value);
    }

    public static TemplateDescription empty() {
        return new TemplateDescription(null);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
