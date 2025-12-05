package com.ryuqq.domain.template.vo;

import java.util.Objects;

/** Template 설명 VO. */
public record TemplateDescription(String value) {

    private static final int MAX_LENGTH = 1000;

    public TemplateDescription(String value) {
        Objects.requireNonNull(value, "TemplateDescription value must not be null");
        String normalized = value.strip();
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Template description must be within " + MAX_LENGTH + " characters");
        }
        this.value = normalized;
    }

    public static TemplateDescription from(String value) {
        return new TemplateDescription(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
