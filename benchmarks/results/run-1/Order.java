package com.company.template.order.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Order Aggregate Root
 *
 * <p>주문을 관리하는 Aggregate Root입니다. 주문의 생성, 확정, 취소 등의 비즈니스 로직을 캡슐화합니다.</p>
 *
 * <p><strong>비즈니스 규칙:</strong></p>
 * <ul>
 *   <li>주문 금액은 항상 0보다 커야 함</li>
 *   <li>PENDING 상태에서만 확정 가능</li>
 *   <li>CONFIRMED 상태에서는 취소 불가능</li>
 * </ul>
 *
 * <p><strong>규칙 준수:</strong></p>
 * <ul>
 *   <li>❌ Lombok 사용 안함 - Pure Java</li>
 *   <li>✅ Law of Demeter - Getter 체이닝 방지</li>
 *   <li>✅ Tell, Don't Ask 패턴 적용</li>
 *   <li>✅ Aggregate 경계 내에서 일관성 보장</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
public class Order {

    // 1. 불변 필드 (final)
    private final OrderId orderId;
    private final CustomerId customerId;
    private final LocalDateTime createdAt;

    // 2. 가변 필드
    private Money amount;
    private OrderStatus status;

    /**
     * Order를 생성합니다 (Private Constructor).
     *
     * <p>외부에서는 {@link #create(OrderId, CustomerId, Money)} 팩토리 메서드를 사용합니다.</p>
     *
     * @param orderId    주문 ID
     * @param customerId 고객 ID
     * @param amount     주문 금액
     * @param createdAt  생성 시각
     * @author Claude
     * @since 2025-10-17
     */
    private Order(OrderId orderId, CustomerId customerId, Money amount, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = OrderStatus.PENDING;
    }

    /**
     * 새로운 Order를 생성합니다 (Factory Method).
     *
     * <p><strong>불변 조건 검증:</strong></p>
     * <ul>
     *   <li>주문 ID는 필수</li>
     *   <li>고객 ID는 필수</li>
     *   <li>주문 금액은 필수 (Money 내부에서 양수 검증)</li>
     * </ul>
     *
     * @param orderId    주문 ID
     * @param customerId 고객 ID
     * @param amount     주문 금액
     * @return 생성된 Order
     * @throws IllegalArgumentException 필수 값이 없거나 유효하지 않을 경우
     * @author Claude
     * @since 2025-10-17
     */
    public static Order create(OrderId orderId, CustomerId customerId, Money amount) {
        Objects.requireNonNull(orderId, "OrderId는 필수입니다");
        Objects.requireNonNull(customerId, "CustomerId는 필수입니다");
        Objects.requireNonNull(amount, "Amount는 필수입니다");

        return new Order(orderId, customerId, amount, LocalDateTime.now());
    }

    /**
     * 주문을 확정합니다.
     *
     * <p><strong>비즈니스 규칙:</strong></p>
     * <ul>
     *   <li>PENDING 상태에서만 확정 가능</li>
     *   <li>확정 후 상태는 CONFIRMED로 변경</li>
     * </ul>
     *
     * <p>Law of Demeter 준수: 상태 전이 로직을 외부에 노출하지 않고 내부 캡슐화</p>
     *
     * @throws IllegalStateException PENDING 상태가 아닐 경우
     * @author Claude
     * @since 2025-10-17
     */
    public void confirm() {
        if (!isConfirmable()) {
            throw new IllegalStateException("PENDING 상태에서만 확정할 수 있습니다. 현재 상태: " + this.status);
        }

        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 주문을 취소합니다.
     *
     * <p><strong>비즈니스 규칙:</strong></p>
     * <ul>
     *   <li>CONFIRMED 상태에서는 취소 불가능</li>
     *   <li>PENDING 상태에서만 취소 가능</li>
     *   <li>취소 후 상태는 CANCELLED로 변경</li>
     * </ul>
     *
     * <p>Law of Demeter 준수: 상태 전이 로직을 외부에 노출하지 않고 내부 캡슐화</p>
     *
     * @throws IllegalStateException 취소 불가능한 상태일 경우
     * @author Claude
     * @since 2025-10-17
     */
    public void cancel() {
        if (!isCancellable()) {
            throw new IllegalStateException("CONFIRMED 상태에서는 취소할 수 없습니다. 현재 상태: " + this.status);
        }

        this.status = OrderStatus.CANCELLED;
    }

    /**
     * 주문이 확정 가능한 상태인지 확인합니다.
     *
     * <p>Law of Demeter 준수: 상태를 묻는 메서드</p>
     * <p>❌ Bad: order.getStatus().equals(PENDING)</p>
     * <p>✅ Good: order.isConfirmable()</p>
     *
     * @return PENDING 상태이면 true, 아니면 false
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isConfirmable() {
        return this.status == OrderStatus.PENDING;
    }

    /**
     * 주문이 취소 가능한 상태인지 확인합니다.
     *
     * <p>Law of Demeter 준수: 상태를 묻는 메서드</p>
     * <p>❌ Bad: order.getStatus() != CONFIRMED</p>
     * <p>✅ Good: order.isCancellable()</p>
     *
     * @return CONFIRMED가 아니면 true, CONFIRMED면 false
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isCancellable() {
        return this.status != OrderStatus.CONFIRMED;
    }

    // 3. Getters (최소한만 노출)

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
     * @return 주문 상태
     * @author Claude
     * @since 2025-10-17
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 주문 생성 시각을 반환합니다.
     *
     * @return 생성 시각
     * @author Claude
     * @since 2025-10-17
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * equals 메서드 (Entity Identity 기반).
     *
     * <p>Order는 orderId로 식별됩니다.</p>
     *
     * @param o 비교 대상 객체
     * @return orderId가 같으면 true
     * @author Claude
     * @since 2025-10-17
     */
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

    /**
     * hashCode 메서드 (Entity Identity 기반).
     *
     * @return orderId의 hashCode
     * @author Claude
     * @since 2025-10-17
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    /**
     * toString 메서드 (디버깅용).
     *
     * @return Order의 문자열 표현
     * @author Claude
     * @since 2025-10-17
     */
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
