package com.company.template.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 주문 Aggregate Root
 *
 * <p>주문과 관련된 모든 비즈니스 로직을 캡슐화하며,
 * Law of Demeter를 준수하여 내부 구조를 외부에 노출하지 않습니다.</p>
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

    private final String orderId;
    private final String customerId;
    private final List<OrderLineItem> lineItems;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderedAt;

    /**
     * Order Aggregate를 생성합니다.
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @author Claude
     * @since 2025-10-17
     */
    public Order(String orderId, String customerId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID는 필수입니다");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID는 필수입니다");
        }

        this.orderId = orderId;
        this.customerId = customerId;
        this.lineItems = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.orderedAt = LocalDateTime.now();
    }

    /**
     * 주문에 상품을 추가합니다.
     *
     * <p>Law of Demeter 준수: 내부 컬렉션을 노출하지 않고 메서드로 캡슐화</p>
     *
     * @param productId 상품 ID
     * @param quantity 수량
     * @param unitPrice 단가
     * @author Claude
     * @since 2025-10-17
     */
    public void addLineItem(String productId, int quantity, BigDecimal unitPrice) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 상품을 추가할 수 있습니다");
        }

        OrderLineItem lineItem = new OrderLineItem(productId, quantity, unitPrice);
        this.lineItems.add(lineItem);
        this.recalculateTotalAmount();
    }

    /**
     * 주문을 확정합니다.
     *
     * <p>Tell, Don't Ask: 외부에서 상태를 확인하는 대신, 주문 스스로 확정 가능 여부를 판단</p>
     *
     * @author Claude
     * @since 2025-10-17
     */
    public void confirm() {
        if (this.lineItems.isEmpty()) {
            throw new IllegalStateException("주문 항목이 없어 확정할 수 없습니다");
        }
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("이미 확정된 주문입니다");
        }

        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 주문을 취소합니다.
     *
     * @author Claude
     * @since 2025-10-17
     */
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("배송된 주문은 취소할 수 없습니다");
        }

        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Law of Demeter 준수: 배송 가능 여부를 묻는 메서드 (Getter 체이닝 방지)
     *
     * <p>❌ Bad: order.getStatus().equals(OrderStatus.CONFIRMED)</p>
     * <p>✅ Good: order.isReadyToShip()</p>
     *
     * @return 배송 가능 여부
     * @author Claude
     * @since 2025-10-17
     */
    public boolean isReadyToShip() {
        return this.status == OrderStatus.CONFIRMED;
    }

    /**
     * Law of Demeter 준수: 최소 주문 금액을 충족하는지 확인
     *
     * <p>❌ Bad: order.getTotalAmount().compareTo(minimum) >= 0</p>
     * <p>✅ Good: order.meetsMinimumAmount(minimum)</p>
     *
     * @param minimumAmount 최소 주문 금액
     * @return 충족 여부
     * @author Claude
     * @since 2025-10-17
     */
    public boolean meetsMinimumAmount(BigDecimal minimumAmount) {
        return this.totalAmount.compareTo(minimumAmount) >= 0;
    }

    /**
     * 총 주문 금액을 재계산합니다 (Private - 내부 응집성)
     *
     * @author Claude
     * @since 2025-10-17
     */
    private void recalculateTotalAmount() {
        this.totalAmount = this.lineItems.stream()
            .map(OrderLineItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters (최소한만 노출 - Aggregate 경계 보호)

    /**
     * 주문 ID를 반환합니다.
     *
     * @return 주문 ID
     * @author Claude
     * @since 2025-10-17
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 고객 ID를 반환합니다.
     *
     * @return 고객 ID
     * @author Claude
     * @since 2025-10-17
     */
    public String getCustomerId() {
        return customerId;
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
     * 총 주문 금액을 반환합니다.
     *
     * @return 총 주문 금액
     * @author Claude
     * @since 2025-10-17
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 주문 항목 목록을 반환합니다 (불변 컬렉션).
     *
     * <p>Law of Demeter: 내부 컬렉션의 직접 수정을 방지</p>
     *
     * @return 주문 항목 목록 (읽기 전용)
     * @author Claude
     * @since 2025-10-17
     */
    public List<OrderLineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }

    /**
     * 주문 일시를 반환합니다.
     *
     * @return 주문 일시
     * @author Claude
     * @since 2025-10-17
     */
    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }
}
