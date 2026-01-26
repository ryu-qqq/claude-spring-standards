package com.ryuqq.domain.convention.id;

/**
 * ConventionId - 컨벤션 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ConventionId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ConventionId forNew() {
        return new ConventionId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ConventionId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ConventionId value must not be null for existing entity");
        }
        return new ConventionId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
