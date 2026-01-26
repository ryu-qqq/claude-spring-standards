package com.ryuqq.domain.configfiletemplate.vo;

/**
 * TemplateContent - 템플릿 내용 Value Object
 *
 * <p>설정 파일의 내용입니다. 변수 치환 가능합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record TemplateContent(String value) {

    public TemplateContent {
        if (value == null) {
            throw new IllegalArgumentException("TemplateContent must not be null");
        }
    }

    public static TemplateContent of(String value) {
        return new TemplateContent(value);
    }

    public static TemplateContent empty() {
        return new TemplateContent("");
    }
}
