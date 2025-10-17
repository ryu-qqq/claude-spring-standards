package com.company.template.order.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * 금액 Value Object
 *
 * <p>금액과 통화 정보를 함께 관리합니다.</p>
 *
 * @param amount 금액 (양수)
 * @param currency 통화 코드
 * @author Claude
 * @since 2025-10-17
 */
public record Money(BigDecimal amount, Currency currency) {
    /**
     * Money를 생성합니다.
     *
     * @param amount 금액
     * @param currency 통화
     * @throws IllegalArgumentException amount가 null이거나 0 이하인 경우, currency가 null인 경우
     */
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Amount must be positive, but got: " + amount
            );
        }
    }

    /**
     * 다른 Money 객체와 금액을 더합니다.
     *
     * @param other 더할 Money 객체
     * @return 합계 Money 객체
     * @throws IllegalArgumentException 통화가 다른 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Other Money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot add different currencies: " + this.currency + " and " + other.currency
            );
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 다른 Money 객체와 금액을 뺍니다.
     *
     * @param other 뺄 Money 객체
     * @return 차액 Money 객체
     * @throws IllegalArgumentException 통화가 다른 경우 또는 결과가 음수인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Other Money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot subtract different currencies: " + this.currency + " and " + other.currency
            );
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Subtraction result must be positive, but got: " + result
            );
        }
        return new Money(result, this.currency);
    }

    /**
     * 이 Money가 다른 Money보다 큰지 확인합니다.
     *
     * @param other 비교할 Money 객체
     * @return 크면 true, 아니면 false
     * @throws IllegalArgumentException 통화가 다른 경우
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Other Money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * 이 Money가 다른 Money보다 작은지 확인합니다.
     *
     * @param other 비교할 Money 객체
     * @return 작으면 true, 아니면 false
     * @throws IllegalArgumentException 통화가 다른 경우
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isLessThan(Money other) {
        Objects.requireNonNull(other, "Other Money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) < 0;
    }
}
