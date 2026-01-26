package com.ryuqq.domain.classtemplate.vo;

/**
 * TemplateCode - 클래스 템플릿 코드
 *
 * <p>플레이스홀더를 포함한 템플릿 코드를 관리합니다. 예: {ClassName}, {Entity}, {bc} 등
 *
 * @author ryu-qqq
 */
public record TemplateCode(String value) {

    public TemplateCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TemplateCode must not be blank");
        }
    }

    public static TemplateCode of(String value) {
        return new TemplateCode(value);
    }
}
