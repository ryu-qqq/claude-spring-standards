package com.ryuqq.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order Aggregate Root
 *
 * 주문 도메인의 핵심 Aggregate로, 주문 생성, 취소, 상태 관리를 담당합니다.
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public class Order {

    private final OrderId orderId;
    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String shippingAddress;
    private final String shippingZipCode;
    private final String shippingCity;
    private OrderStatus status;
    private final List<OrderLineItem> lineItems;
    private final BigDecimal totalAmount;
    private final LocalDateTime orderedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    /**
     * Order 생성자 (외부에서 직접 호출 금지)
     */
    protected Order(
        OrderId orderId,
        Long customerId,
        String customerName,
        String customerEmail,
        String shippingAddress,
        String shippingZipCode,
        String shippingCity,
        List<OrderLineItem> lineItems
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.shippingZipCode = shippingZipCode;
        this.shippingCity = shippingCity;
        this.status = OrderStatus.PLACED;
        this.lineItems = new ArrayList<>(lineItems);
        this.totalAmount = calculateTotalAmount(lineItems);
        this.orderedAt = LocalDateTime.now();
    }

    /**
     * 새로운 주문 생성 (Static Factory Method)
     *
     * @param orderId 주문 ID
     * @param customerId 고객 ID (Long FK 전략)
     * @param customerName 고객 이름
     * @param customerEmail 고객 이메일
     * @param shippingAddress 배송 주소
     * @param shippingZipCode 우편번호
     * @param shippingCity 도시
     * @param lineItems 주문 항목 목록
     * @return 생성된 Order
     */
    public static Order placeOrder(
        OrderId orderId,
        Long customerId,
        String customerName,
        String customerEmail,
        String shippingAddress,
        String shippingZipCode,
        String shippingCity,
        List<OrderLineItem> lineItems
    ) {
        validateOrderCreation(customerId, customerName, customerEmail, shippingAddress, lineItems);

        return new Order(
            orderId,
            customerId,
            customerName,
            customerEmail,
            shippingAddress,
            shippingZipCode,
            shippingCity,
            lineItems
        );
    }

    /**
     * 주문 취소 (Tell, Don't Ask)
     *
     * PLACED 상태에서만 취소 가능
     *
     * @param reason 취소 사유
     * @throws IllegalStateException PLACED 상태가 아닐 경우
     */
    public void cancelOrder(String reason) {
        if (this.status != OrderStatus.PLACED) {
            throw new IllegalStateException(
                "주문 취소는 PLACED 상태에서만 가능합니다. 현재 상태: " + this.status
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }

        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    /**
     * 주문 확인
     *
     * PLACED 상태에서 CONFIRMED로 전환
     *
     * @throws IllegalStateException PLACED 상태가 아닐 경우
     */
    public void confirmOrder() {
        if (this.status != OrderStatus.PLACED) {
            throw new IllegalStateException(
                "주문 확인은 PLACED 상태에서만 가능합니다. 현재 상태: " + this.status
            );
        }

        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 고객 우편번호 조회 (Law of Demeter 준수)
     *
     * Getter 체이닝 방지: order.getCustomer().getAddress().getZip() ❌
     *
     * @return 우편번호
     */
    public String getCustomerZipCode() {
        return this.shippingZipCode;
    }

    /**
     * 고객 주소 전체 정보 조회 (Law of Demeter 준수)
     *
     * @return 주소 문자열 (주소 + 우편번호 + 도시)
     */
    public String getFullShippingAddress() {
        return String.format("%s, %s, %s",
            this.shippingAddress,
            this.shippingZipCode,
            this.shippingCity
        );
    }

    /**
     * 주문이 취소 가능한지 확인
     *
     * @return 취소 가능 여부
     */
    public boolean isCancellable() {
        return this.status == OrderStatus.PLACED;
    }

    /**
     * 주문 총액 계산 (Private Helper)
     */
    private static BigDecimal calculateTotalAmount(List<OrderLineItem> lineItems) {
        return lineItems.stream()
            .map(OrderLineItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 주문 생성 검증 (Private Helper)
     */
    private static void validateOrderCreation(
        Long customerId,
        String customerName,
        String customerEmail,
        String shippingAddress,
        List<OrderLineItem> lineItems
    ) {
        if (customerId == null) {
            throw new IllegalArgumentException("고객 ID는 필수입니다.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("고객 이름은 필수입니다.");
        }
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("고객 이메일은 필수입니다.");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("배송 주소는 필수입니다.");
        }
        if (lineItems == null || lineItems.isEmpty()) {
            throw new IllegalArgumentException("주문 항목은 최소 1개 이상이어야 합니다.");
        }
    }

    // Getters (Lombok 사용 금지)

    public OrderId getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }
}
