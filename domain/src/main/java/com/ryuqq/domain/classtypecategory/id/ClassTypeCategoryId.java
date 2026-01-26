package com.ryuqq.domain.classtypecategory.id;

/**
 * ClassTypeCategoryId - 클래스 타입 카테고리 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ClassTypeCategoryId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ClassTypeCategoryId forNew() {
        return new ClassTypeCategoryId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ClassTypeCategoryId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ClassTypeCategoryId value must not be null for existing entity");
        }
        return new ClassTypeCategoryId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
