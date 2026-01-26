package com.ryuqq.domain.module.id;

/**
 * ModuleId - 모듈 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ModuleId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ModuleId forNew() {
        return new ModuleId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ModuleId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ModuleId value must not be null for existing entity");
        }
        return new ModuleId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
