package com.company.template.order.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order Aggregate Root
 *
 * <p>주문의 생명주기와 비즈니스 규칙을 관리하는 Aggregate Root입니다.
 * Law of Demeter를 준수하며, Getter 체이닝을 금지합니다.
 *
 * @author Claude
 * @since 2025-10-17
 */
public class Order {

    private final OrderId orderId;
    private final CustomerId customerId;
    private final List<OrderItem> orderItems;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Order 생성자
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param orderItems 주문 항목 리스트
     */
    private Order(OrderId orderId, CustomerId customerId, List<OrderItem> orderItems) {
        validateOrderItems(orderItems);
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderItems = new ArrayList<>(orderItems);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 생성 (Factory Method)
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param orderItems 주문 항목 리스트
     * @return 생성된 Order 인스턴스
     */
    public static Order placeOrder(OrderId orderId, CustomerId customerId, List<OrderItem> orderItems) {
        return new Order(orderId, customerId, orderItems);
    }

    /**
     * 주문 취소
     *
     * <p>PENDING 또는 CONFIRMED 상태에서만 취소 가능합니다.
     *
     * @throws IllegalStateException 취소 불가능한 상태인 경우
     */
    public void cancelOrder() {
        if (!canCancel()) {
            throw new IllegalStateException("Cannot cancel order in status: " + this.status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 확정
     */
    public void confirmOrder() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm order in status: " + this.status);
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 배송 시작
     */
    public void shipOrder() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot ship order in status: " + this.status);
        }
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 배송 완료
     */
    public void deliverOrder() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot deliver order in status: " + this.status);
        }
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 항목 추가
     *
     * <p>PENDING 상태에서만 추가 가능합니다.
     *
     * @param orderItem 추가할 주문 항목
     */
    public void addOrderItem(OrderItem orderItem) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add item to order in status: " + this.status);
        }
        this.orderItems.add(orderItem);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 총 주문 금액 계산 (Tell, Don't Ask 패턴)
     *
     * @return 총 주문 금액
     */
    public Money calculateTotalAmount() {
        Money total = Money.zero();
        for (OrderItem item : this.orderItems) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    /**
     * 주문 취소 가능 여부 검증
     *
     * @return 취소 가능하면 true
     */
    private boolean canCancel() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }

    /**
     * 주문 항목 리스트 검증
     *
     * @param orderItems 검증할 주문 항목 리스트
     * @throws IllegalArgumentException 주문 항목이 비어있는 경우
     */
    private void validateOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }

    // Getters (No Setter - Immutability)

    public OrderId getOrderId() {
        return this.orderId;
    }

    public CustomerId getCustomerId() {
        return this.customerId;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(this.orderItems);
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
