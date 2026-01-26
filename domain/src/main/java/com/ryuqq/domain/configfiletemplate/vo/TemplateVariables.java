package com.ryuqq.domain.configfiletemplate.vo;

/**
 * TemplateVariables - 템플릿 변수 정의 Value Object
 *
 * <p>치환 가능한 변수들을 JSON 형태로 저장합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record TemplateVariables(String value) {

    public static TemplateVariables of(String value) {
        return new TemplateVariables(value);
    }

    public static TemplateVariables empty() {
        return new TemplateVariables(null);
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
