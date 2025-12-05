package com.ryuqq.domain.template.vo;

import java.util.Objects;
import java.util.UUID;

/** Template Section 식별자 VO. */
public record TemplateSectionId(UUID value) {

    public TemplateSectionId {
        Objects.requireNonNull(value, "TemplateSectionId value must not be null");
    }

    public static TemplateSectionId newId() {
        return new TemplateSectionId(UUID.randomUUID());
    }

    public static TemplateSectionId from(UUID value) {
        return new TemplateSectionId(value);
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return asString();
    }
}
