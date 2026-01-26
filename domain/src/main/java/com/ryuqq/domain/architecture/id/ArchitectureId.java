package com.ryuqq.domain.architecture.id;

/**
 * ArchitectureId - 아키텍처 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ArchitectureId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ArchitectureId forNew() {
        return new ArchitectureId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ArchitectureId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ArchitectureId value must not be null for existing entity");
        }
        return new ArchitectureId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
