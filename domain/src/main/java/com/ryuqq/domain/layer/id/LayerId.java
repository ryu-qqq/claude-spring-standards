package com.ryuqq.domain.layer.id;

/**
 * LayerId - 레이어 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record LayerId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static LayerId forNew() {
        return new LayerId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static LayerId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "LayerId value must not be null for existing entity");
        }
        return new LayerId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
