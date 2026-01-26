package com.ryuqq.domain.techstack.id;

/**
 * TechStackId - 기술 스택 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record TechStackId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static TechStackId forNew() {
        return new TechStackId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static TechStackId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "TechStackId value must not be null for existing entity");
        }
        return new TechStackId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
