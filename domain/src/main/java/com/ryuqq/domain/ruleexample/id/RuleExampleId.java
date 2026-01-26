package com.ryuqq.domain.ruleexample.id;

/**
 * RuleExampleId - 규칙 예시 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record RuleExampleId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static RuleExampleId forNew() {
        return new RuleExampleId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static RuleExampleId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "RuleExampleId value must not be null for existing entity");
        }
        return new RuleExampleId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
