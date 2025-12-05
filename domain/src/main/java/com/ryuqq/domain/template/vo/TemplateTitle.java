package com.ryuqq.domain.template.vo;

import java.util.Objects;

/** Template 제목 VO. */
public record TemplateTitle(String value) {

    private static final int MAX_LENGTH = 120;

    public TemplateTitle(String value) {
        Objects.requireNonNull(value, "TemplateTitle value must not be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Template title must not be blank");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Template title must be within " + MAX_LENGTH + " characters");
        }
        this.value = normalized;
    }

    public static TemplateTitle from(String value) {
        return new TemplateTitle(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
