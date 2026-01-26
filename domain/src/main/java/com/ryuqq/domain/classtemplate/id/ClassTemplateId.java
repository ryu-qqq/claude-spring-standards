package com.ryuqq.domain.classtemplate.id;

/**
 * ClassTemplateId - 클래스 템플릿 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ClassTemplateId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ClassTemplateId forNew() {
        return new ClassTemplateId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ClassTemplateId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ClassTemplateId value must not be null for existing entity");
        }
        return new ClassTemplateId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
