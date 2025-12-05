package com.ryuqq.domain.template.vo;

import java.util.Objects;
import java.util.UUID;

/**
 * Template 식별자 VO.
 *
 * <p>UUID 기반으로 생성되며 null을 허용하지 않습니다.
 */
public record TemplateId(UUID value) {

    public TemplateId {
        Objects.requireNonNull(value, "TemplateId value must not be null");
    }

    public static TemplateId newId() {
        return new TemplateId(UUID.randomUUID());
    }

    public static TemplateId from(UUID value) {
        return new TemplateId(value);
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public String toString() {
        return asString();
    }
}
