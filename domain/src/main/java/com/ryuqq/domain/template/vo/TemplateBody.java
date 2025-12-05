package com.ryuqq.domain.template.vo;

import java.util.Objects;

/** Template Section 본문 VO. */
public record TemplateBody(String value) {

    private static final int MAX_LENGTH = 4000;

    public TemplateBody(String value) {
        Objects.requireNonNull(value, "TemplateBody value must not be null");
        String normalized = value.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Template body must not be blank");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Template body must be within " + MAX_LENGTH + " characters");
        }
        this.value = normalized;
    }

    public static TemplateBody from(String value) {
        return new TemplateBody(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
