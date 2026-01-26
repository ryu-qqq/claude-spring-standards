package com.ryuqq.domain.layerdependency.id;

/**
 * LayerDependencyRuleId - 레이어 의존성 규칙 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record LayerDependencyRuleId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static LayerDependencyRuleId forNew() {
        return new LayerDependencyRuleId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static LayerDependencyRuleId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "LayerDependencyRuleId value must not be null for existing entity");
        }
        return new LayerDependencyRuleId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
