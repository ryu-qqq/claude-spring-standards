package com.ryuqq.domain.packagepurpose.id;

/**
 * PackagePurposeId - 패키지 목적 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record PackagePurposeId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static PackagePurposeId forNew() {
        return new PackagePurposeId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static PackagePurposeId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "PackagePurposeId value must not be null for existing entity");
        }
        return new PackagePurposeId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
