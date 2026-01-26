package com.ryuqq.domain.resourcetemplate.id;

/**
 * ResourceTemplateId - 리소스 템플릿 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ResourceTemplateId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ResourceTemplateId forNew() {
        return new ResourceTemplateId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ResourceTemplateId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ResourceTemplateId value must not be null for existing entity");
        }
        return new ResourceTemplateId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
