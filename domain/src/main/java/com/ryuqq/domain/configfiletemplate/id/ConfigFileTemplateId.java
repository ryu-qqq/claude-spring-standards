package com.ryuqq.domain.configfiletemplate.id;

/**
 * ConfigFileTemplateId - 설정 파일 템플릿 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileTemplateId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ConfigFileTemplateId forNew() {
        return new ConfigFileTemplateId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ConfigFileTemplateId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ConfigFileTemplateId value must not be null for existing entity");
        }
        return new ConfigFileTemplateId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
