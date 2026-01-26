package com.ryuqq.domain.zerotolerance.id;

/**
 * ZeroToleranceRuleId - Zero Tolerance 규칙 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ZeroToleranceRuleId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ZeroToleranceRuleId forNew() {
        return new ZeroToleranceRuleId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ZeroToleranceRuleId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ZeroToleranceRuleId value must not be null for existing entity");
        }
        return new ZeroToleranceRuleId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
