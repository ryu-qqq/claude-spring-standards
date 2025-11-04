package com.ryuqq.domain.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Domain (Hook OFF 스타일)
 *
 * ❌ Lombok 사용
 * ❌ Public Constructor
 * ❌ Getter 체이닝 가능
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String orderId;
    private Customer customer;  // ❌ 직접 참조 (Getter 체이닝 가능)
    private Address shippingAddress;  // ❌ 직접 참조
    private OrderStatus status;
    private List<OrderLineItem> lineItems;
    private BigDecimal totalAmount;
    private LocalDateTime orderedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    // ❌ Getter 체이닝 발생 가능
    // order.getCustomer().getAddress().getZip()

    public void placeOrder() {
        this.status = OrderStatus.PLACED;
        this.orderedAt = LocalDateTime.now();
        this.totalAmount = calculateTotal();
    }

    public void cancelOrder(String reason) {
        if (this.status != OrderStatus.PLACED) {
            throw new IllegalStateException("Only PLACED orders can be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    private BigDecimal calculateTotal() {
        return lineItems.stream()
            .map(OrderLineItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// ❌ Customer, Address 객체 직접 참조 (Law of Demeter 위반 가능)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Customer {
    private Long customerId;
    private String name;
    private String email;
    private Address address;  // ❌ Getter 체이닝: order.getCustomer().getAddress()
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Address {
    private String street;
    private String zipCode;
    private String city;

    // ❌ Getter 체이닝: order.getCustomer().getAddress().getZipCode()
}
