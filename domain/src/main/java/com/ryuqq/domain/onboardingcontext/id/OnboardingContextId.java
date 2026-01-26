package com.ryuqq.domain.onboardingcontext.id;

/**
 * OnboardingContextId - 온보딩 컨텍스트 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record OnboardingContextId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static OnboardingContextId forNew() {
        return new OnboardingContextId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static OnboardingContextId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "OnboardingContextId value must not be null for existing entity");
        }
        return new OnboardingContextId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
