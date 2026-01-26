package com.ryuqq.domain.archunittest.id;

/**
 * ArchUnitTestId - ArchUnit 테스트 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ArchUnitTestId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ArchUnitTestId forNew() {
        return new ArchUnitTestId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ArchUnitTestId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ArchUnitTestId value must not be null for existing entity");
        }
        return new ArchUnitTestId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
