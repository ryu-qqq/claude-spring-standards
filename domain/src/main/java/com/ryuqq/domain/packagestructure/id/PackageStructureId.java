package com.ryuqq.domain.packagestructure.id;

/**
 * PackageStructureId - 패키지 구조 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record PackageStructureId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static PackageStructureId forNew() {
        return new PackageStructureId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static PackageStructureId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "PackageStructureId value must not be null for existing entity");
        }
        return new PackageStructureId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
