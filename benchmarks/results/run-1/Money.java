package com.company.template.order.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Money Value Object
 *
 * <p>금액을 표현하는 Value Object입니다. 금액과 통화를 함께 관리합니다.</p>
 *
 * <p><strong>불변성:</strong></p>
 * <ul>
 *   <li>Value Object는 불변 객체</li>
 *   <li>연산(add, subtract) 시 새로운 인스턴스 반환</li>
 *   <li>원본 객체는 변경되지 않음</li>
 * </ul>
 *
 * <p><strong>비즈니스 규칙:</strong></p>
 * <ul>
 *   <li>금액은 항상 양수 (0 초과)</li>
 *   <li>통화 단위는 필수</li>
 *   <li>같은 통화끼리만 연산 가능</li>
 * </ul>
 *
 * @param amount   금액 (양수)
 * @param currency 통화 단위
 * @author Claude
 * @since 2025-10-17
 */
public record Money(BigDecimal amount, Currency currency) {

    /**
     * Compact Constructor - 유효성 검증.
     *
     * <p><strong>검증 규칙:</strong></p>
     * <ul>
     *   <li>amount는 null 불가</li>
     *   <li>currency는 null 불가</li>
     *   <li>amount는 0보다 커야 함</li>
     * </ul>
     *
     * @throws IllegalArgumentException amount나 currency가 null이거나 amount가 0 이하일 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money {
        Objects.requireNonNull(amount, "Amount는 null일 수 없습니다");
        Objects.requireNonNull(currency, "Currency는 null일 수 없습니다");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount는 0보다 커야 합니다. 입력값: " + amount);
        }
    }

    /**
     * 두 Money를 더합니다.
     *
     * <p><strong>불변성 보장:</strong></p>
     * <ul>
     *   <li>새로운 Money 인스턴스 반환</li>
     *   <li>원본 객체는 변경되지 않음</li>
     * </ul>
     *
     * @param other 더할 Money
     * @return 합산된 새 Money 인스턴스
     * @throws IllegalArgumentException other가 null이거나 통화가 다를 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "더할 Money는 null일 수 없습니다");
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 두 Money를 뺍니다.
     *
     * <p><strong>불변성 보장:</strong></p>
     * <ul>
     *   <li>새로운 Money 인스턴스 반환</li>
     *   <li>원본 객체는 변경되지 않음</li>
     * </ul>
     *
     * <p><strong>주의:</strong></p>
     * <ul>
     *   <li>결과가 음수일 수 있음 (검증하지 않음)</li>
     *   <li>필요시 호출자가 결과 검증</li>
     * </ul>
     *
     * @param other 뺄 Money
     * @return 차감된 새 Money 인스턴스
     * @throws IllegalArgumentException other가 null이거나 통화가 다를 경우
     * @author Claude
     * @since 2025-10-17
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "뺄 Money는 null일 수 없습니다");
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * 통화가 같은지 검증합니다.
     *
     * @param other 비교할 Money
     * @throws IllegalArgumentException 통화가 다를 경우
     * @author Claude
     * @since 2025-10-17
     */
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "통화가 다른 Money끼리는 연산할 수 없습니다. " +
                            "현재: " + this.currency + ", 대상: " + other.currency
            );
        }
    }
}
