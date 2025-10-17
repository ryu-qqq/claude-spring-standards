package com.company.template.order.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Money Value Object
 *
 * <p>금액과 통화 정보를 캡슐화한 불변 객체입니다.</p>
 *
 * <p><strong>규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Java Record 사용 (불변성 보장)</li>
 *   <li>✅ 생성자에서 유효성 검증</li>
 *   <li>✅ 금액 연산 메서드 제공 (add, subtract)</li>
 *   <li>✅ Law of Demeter - 내부 구조 캡슐화</li>
 * </ul>
 *
 * @param amount 금액 (양수)
 * @param currency 통화 (예: KRW, USD)
 * @author Claude
 * @since 2025-10-17
 */
public record Money(BigDecimal amount, Currency currency) {

    /**
     * Money의 compact 생성자 (유효성 검증).
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>amount null 불가</li>
     *   <li>currency null 불가</li>
     *   <li>amount는 0보다 커야 함</li>
     * </ul>
     *
     * @throws NullPointerException amount 또는 currency가 null인 경우
     * @throws IllegalArgumentException amount가 0 이하인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Amount must be positive, but was: " + amount
            );
        }
    }

    /**
     * 다른 Money를 더합니다.
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>통화가 동일해야 함</li>
     *   <li>새로운 Money 객체 반환 (불변성 유지)</li>
     * </ul>
     *
     * @param other 더할 Money
     * @return 합산된 새로운 Money
     * @throws IllegalArgumentException 통화가 다른 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot add money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 다른 Money를 뺍니다.
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>통화가 동일해야 함</li>
     *   <li>결과는 0보다 커야 함</li>
     *   <li>새로운 Money 객체 반환 (불변성 유지)</li>
     * </ul>
     *
     * @param other 뺄 Money
     * @return 차감된 새로운 Money
     * @throws IllegalArgumentException 통화가 다르거나 결과가 0 이하인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot subtract money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Subtraction result must be positive, but was: " + result
            );
        }
        return new Money(result, this.currency);
    }

    /**
     * 이 Money가 다른 Money보다 큰지 비교합니다.
     *
     * <p>통화가 동일해야 비교 가능합니다.</p>
     *
     * @param other 비교할 Money
     * @return 이 Money가 더 크면 true
     * @throws IllegalArgumentException 통화가 다른 경우
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * 이 Money가 다른 Money보다 작은지 비교합니다.
     *
     * <p>통화가 동일해야 비교 가능합니다.</p>
     *
     * @param other 비교할 Money
     * @return 이 Money가 더 작으면 true
     * @throws IllegalArgumentException 통화가 다른 경우
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isLessThan(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Money를 문자열로 반환합니다.
     *
     * @return Money 문자열 표현 (예: "50000 KRW")
     * @author Claude
     * @since 2025-10-17
     */
    @Override
    public String toString() {
        return amount + " " + currency.getCurrencyCode();
    }
}
