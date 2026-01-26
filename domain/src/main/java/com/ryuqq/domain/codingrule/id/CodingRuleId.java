package com.ryuqq.domain.codingrule.id;

/**
 * CodingRuleId - 코딩 규칙 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record CodingRuleId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static CodingRuleId forNew() {
        return new CodingRuleId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static CodingRuleId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "CodingRuleId value must not be null for existing entity");
        }
        return new CodingRuleId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
