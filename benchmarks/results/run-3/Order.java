package com.company.template.order.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Order Aggregate Root
 *
 * <p>주문 생성, 확정, 취소 등의 주문 생명주기를 관리하는 핵심 Aggregate입니다.</p>
 *
 * <p><strong>규칙 준수:</strong></p>
 * <ul>
 *   <li>❌ Lombok 사용 안함 - Pure Java</li>
 *   <li>✅ Law of Demeter - Getter 체이닝 방지</li>
 *   <li>✅ Tell, Don't Ask 패턴 적용</li>
 *   <li>✅ Aggregate 경계 내에서 일관성 보장</li>
 * </ul>
 *
 * <p><strong>불변 조건 (Invariants):</strong></p>
 * <ul>
 *   <li>주문 금액은 항상 0보다 커야 함</li>
 *   <li>상태 전이는 비즈니스 규칙을 따라야 함 (PENDING → CONFIRMED, PENDING/CONFIRMED → CANCELLED)</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
public class Order {

    // 1. 불변 필드 (final)
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money amount;
    private final LocalDateTime createdAt;

    // 2. 가변 필드
    private OrderStatus status;

    /**
     * Order를 생성합니다 (private 생성자 - factory method 사용 강제).
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param amount 주문 금액
     * @param createdAt 생성 시간
     * @author Claude
     * @since 2025-10-17
     */
    private Order(OrderId orderId, CustomerId customerId, Money amount, LocalDateTime createdAt) {
        this.orderId = Objects.requireNonNull(orderId, "OrderId cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "CustomerId cannot be null");
        this.amount = Objects.requireNonNull(amount, "Money cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * 새로운 Order를 생성합니다 (Factory Method).
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>주문 금액은 0보다 커야 함 (Money Value Object에서 검증)</li>
     *   <li>초기 상태는 PENDING</li>
     * </ul>
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param amount 주문 금액
     * @return 생성된 Order
     * @author Claude
     * @since 2025-10-17
     */
    public static Order create(OrderId orderId, CustomerId customerId, Money amount) {
        return new Order(orderId, customerId, amount, LocalDateTime.now());
    }

    /**
     * 주문을 확정 상태로 변경합니다.
     *
     * <p>Law of Demeter 준수: 상태를 묻지 말고 명령을 내림</p>
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>PENDING 상태에서만 확정 가능</li>
     *   <li>CONFIRMED 또는 CANCELLED 상태에서는 예외 발생</li>
     * </ul>
     *
     * @throws IllegalStateException PENDING 상태가 아닌 경우
     * @author Claude
     * @since 2025-10-17
     */
    public void confirm() {
        if (!isConfirmable()) {
            throw new IllegalStateException(
                "주문 확정은 PENDING 상태에서만 가능합니다. 현재 상태: " + this.status
            );
        }
        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 주문을 취소 상태로 변경합니다.
     *
     * <p>Law of Demeter 준수: 상태를 묻지 말고 명령을 내림</p>
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>PENDING 또는 CONFIRMED 상태에서 취소 가능</li>
     *   <li>이미 CANCELLED 상태인 경우 예외 발생</li>
     * </ul>
     *
     * @throws IllegalStateException 취소 불가능한 상태인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public void cancel() {
        if (!isCancellable()) {
            throw new IllegalStateException(
                "이미 취소된 주문입니다. 현재 상태: " + this.status
            );
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * 주문 확정 가능 여부를 확인합니다.
     *
     * <p>Law of Demeter: 상태를 묻는 메서드</p>
     * <p>❌ Bad: order.getStatus().equals(PENDING)</p>
     * <p>✅ Good: order.isConfirmable()</p>
     *
     * @return 확정 가능 여부
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isConfirmable() {
        return this.status == OrderStatus.PENDING;
    }

    /**
     * 주문 취소 가능 여부를 확인합니다.
     *
     * <p>Law of Demeter: 상태를 묻는 메서드</p>
     * <p>❌ Bad: order.getStatus() != CANCELLED</p>
     * <p>✅ Good: order.isCancellable()</p>
     *
     * @return 취소 가능 여부
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isCancellable() {
        return this.status != OrderStatus.CANCELLED;
    }

    /**
     * 주문이 확정 상태인지 확인합니다.
     *
     * @return 확정 상태 여부
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isConfirmed() {
        return this.status == OrderStatus.CONFIRMED;
    }

    /**
     * 주문이 취소 상태인지 확인합니다.
     *
     * @return 취소 상태 여부
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isCancelled() {
        return this.status == OrderStatus.CANCELLED;
    }

    // Getters (최소한만 노출)

    /**
     * 주문 ID를 반환합니다.
     *
     * @return 주문 ID
     * @author Claude
     * @since 2025-10-17
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * 고객 ID를 반환합니다.
     *
     * @return 고객 ID
     * @author Claude
     * @since 2025-10-17
     */
    public CustomerId getCustomerId() {
        return customerId;
    }

    /**
     * 주문 금액을 반환합니다.
     *
     * @return 주문 금액
     * @author Claude
     * @since 2025-10-17
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * 주문 상태를 반환합니다.
     *
     * <p>주의: Law of Demeter를 준수하려면 isConfirmable(), isCancellable() 등의 메서드 사용 권장</p>
     *
     * @return 주문 상태
     * @author Claude
     * @since 2025-10-17
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 주문 생성 시간을 반환합니다.
     *
     * @return 생성 시간
     * @author Claude
     * @since 2025-10-17
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 주문 금액의 숫자 값만 반환합니다 (외부 시스템 연동용).
     *
     * <p>Law of Demeter 준수: amount.getAmount() 체이닝 방지</p>
     *
     * @return 주문 금액 숫자 값
     * @author Claude
     * @since 2025-10-17
     */
    public BigDecimal getAmountValue() {
        return this.amount.amount();
    }

    /**
     * 주문 통화 정보를 반환합니다 (외부 시스템 연동용).
     *
     * <p>Law of Demeter 준수: amount.getCurrency() 체이닝 방지</p>
     *
     * @return 통화 정보
     * @author Claude
     * @since 2025-10-17
     */
    public String getCurrencyCode() {
        return this.amount.currency().getCurrencyCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{"
            + "orderId=" + orderId
            + ", customerId=" + customerId
            + ", amount=" + amount
            + ", status=" + status
            + ", createdAt=" + createdAt
            + '}';
    }
}
