package com.ryuqq.domain.template2.vo;

/**
 * Money Value Object
 *
 * <p><strong>도메인 규칙</strong>: 금액은 0 이상이어야 한다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record Money(Long amount) {

    public static final Money ZERO = Money.of(0L);

    /** Compact Constructor (검증 로직) */
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 null일 수 없습니다.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다: " + amount);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param amount 금액 (null 불가, 0 이상)
     * @return Money
     * @throws IllegalArgumentException amount가 null이거나 음수인 경우
     */
    public static Money of(Long amount) {
        return new Money(amount);
    }

    /**
     * 금액 더하기
     *
     * @param other 더할 금액
     * @return 합계
     */
    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    /**
     * 금액 빼기
     *
     * @param other 뺄 금액
     * @return 차액
     * @throws IllegalArgumentException 결과가 음수인 경우
     */
    public Money subtract(Money other) {
        return new Money(this.amount - other.amount);
    }

    /**
     * 금액 곱하기
     *
     * @param multiplier 배수
     * @return 곱셈 결과
     */
    public Money multiply(int multiplier) {
        return new Money(this.amount * multiplier);
    }

    /**
     * 금액 비교 (큰지)
     *
     * @param other 비교 대상
     * @return this가 크면 true
     */
    public boolean isGreaterThan(Money other) {
        return this.amount > other.amount;
    }

    /**
     * 금액 비교 (작은지)
     *
     * @param other 비교 대상
     * @return this가 작으면 true
     */
    public boolean isLessThan(Money other) {
        return this.amount < other.amount;
    }
}
