package com.ryuqq.domain.resourcetemplate.vo;

/**
 * TemplateCategory - 템플릿 카테고리 Value Object
 *
 * <p>리소스 템플릿의 용도별 분류를 정의합니다.
 *
 * @author ryu-qqq
 */
public enum TemplateCategory {
    CONFIG("설정 파일"),
    I18N("다국어 파일"),
    STATIC("정적 리소스"),
    BUILD("빌드 설정");

    private final String description;

    TemplateCategory(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isConfig() {
        return this == CONFIG;
    }

    public boolean isI18n() {
        return this == I18N;
    }

    public boolean isStatic() {
        return this == STATIC;
    }

    public boolean isBuild() {
        return this == BUILD;
    }
}
