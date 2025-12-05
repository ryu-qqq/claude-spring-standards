package com.ryuqq.domain.template.vo;

/** Template 버전 VO. */
public record TemplateVersion(long value) {

    public TemplateVersion {
        if (value < 1) {
            throw new IllegalArgumentException("Template version must be positive");
        }
    }

    public static TemplateVersion initial() {
        return new TemplateVersion(1L);
    }

    public TemplateVersion next() {
        return new TemplateVersion(Math.addExact(value, 1L));
    }
}
