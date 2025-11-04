package com.ryuqq.application.order.dto.response;

import com.ryuqq.domain.order.Order;
import com.ryuqq.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Response DTO
 *
 * 주문 정보 응답 데이터
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public class OrderResponse {

    private final String orderId;
    private final Long customerId;
    private final String customerName;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final LocalDateTime orderedAt;

    private OrderResponse(
        String orderId,
        Long customerId,
        String customerName,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime orderedAt
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderedAt = orderedAt;
    }

    /**
     * Domain Order로부터 Response 생성
     *
     * @param order Domain Order
     * @return OrderResponse
     */
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getOrderId().getValue(),
            order.getCustomerId(),
            order.getCustomerName(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getOrderedAt()
        );
    }

    // Getters (Lombok 사용 금지)

    public String getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }
}
