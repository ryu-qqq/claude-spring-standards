package com.company.template.order.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Order Aggregate Root
 *
 * <p>주문 생성, 확정, 취소 등의 비즈니스 로직을 캡슐화합니다.</p>
 *
 * <p><strong>규칙 준수:</strong></p>
 * <ul>
 *   <li>❌ Lombok 사용 안함 - Pure Java</li>
 *   <li>✅ Law of Demeter - Getter 체이닝 방지</li>
 *   <li>✅ Tell, Don't Ask 패턴 적용</li>
 *   <li>✅ Aggregate 경계 내에서 일관성 보장</li>
 * </ul>
 *
 * <p><strong>비즈니스 규칙:</strong></p>
 * <ul>
 *   <li>주문 금액은 항상 0보다 커야 함</li>
 *   <li>PENDING 상태에서만 확정 가능</li>
 *   <li>CONFIRMED 상태에서는 취소 불가능</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
public class Order {

    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money amount;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    /**
     * Order를 생성합니다 (Private Constructor).
     *
     * <p>외부에서는 정적 팩토리 메서드 {@link #create}를 사용합니다.</p>
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param amount 금액
     * @param status 상태
     * @param createdAt 생성 시각
     */
    private Order(
        OrderId orderId,
        CustomerId customerId,
        Money amount,
        OrderStatus status,
        LocalDateTime createdAt
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * 새로운 주문을 생성합니다.
     *
     * <p>주문은 PENDING 상태로 시작합니다.</p>
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param amount 금액
     * @return 생성된 Order 객체
     * @throws IllegalArgumentException orderId, customerId, amount가 null인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public static Order create(OrderId orderId, CustomerId customerId, Money amount) {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(customerId, "CustomerId cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        return new Order(
            orderId,
            customerId,
            amount,
            OrderStatus.PENDING,
            LocalDateTime.now()
        );
    }

    /**
     * 주문을 확정합니다.
     *
     * <p>Law of Demeter 준수: 상태 전이 로직을 내부에 캡슐화</p>
     *
     * <p><strong>비즈니스 규칙:</strong></p>
     * <ul>
     *   <li>PENDING 상태에서만 확정 가능</li>
     * </ul>
     *
     * @throws IllegalStateException PENDING 상태가 아닌 경우
     * @author Claude
     * @since 2025-10-17
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                "Only PENDING orders can be confirmed. Current status: " + this.status
            );
        }
        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 주문을 취소합니다.
     *
     * <p>Law of Demeter 준수: 상태 전이 로직을 내부에 캡슐화</p>
     *
     * <p><strong>비즈니스 규칙:</strong></p>
     * <ul>
     *   <li>CONFIRMED 상태에서는 취소 불가능</li>
     * </ul>
     *
     * @throws IllegalStateException CONFIRMED 상태인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public void cancel() {
        if (this.status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Cannot cancel CONFIRMED orders. Current status: " + this.status
            );
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * 주문이 확정 가능한지 확인합니다.
     *
     * <p>Law of Demeter 준수: 상태를 묻는 메서드</p>
     *
     * <p>❌ Bad: order.getStatus().equals(OrderStatus.PENDING)</p>
     * <p>✅ Good: order.isConfirmable()</p>
     *
     * @return 확정 가능하면 true, 아니면 false
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isConfirmable() {
        return this.status == OrderStatus.PENDING;
    }

    /**
     * 주문이 취소 가능한지 확인합니다.
     *
     * <p>Law of Demeter 준수: 상태를 묻는 메서드</p>
     *
     * <p>❌ Bad: order.getStatus() != OrderStatus.CONFIRMED</p>
     * <p>✅ Good: order.isCancellable()</p>
     *
     * @return 취소 가능하면 true, 아니면 false
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isCancellable() {
        return this.status != OrderStatus.CONFIRMED;
    }

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
     * 금액을 반환합니다.
     *
     * @return 금액
     * @author Claude
     * @since 2025-10-17
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * 주문 상태를 반환합니다.
     *
     * @return 주문 상태
     * @author Claude
     * @since 2025-10-17
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 생성 시각을 반환합니다.
     *
     * @return 생성 시각
     * @author Claude
     * @since 2025-10-17
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
