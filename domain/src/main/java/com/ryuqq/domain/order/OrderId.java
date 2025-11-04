package com.ryuqq.domain.order;

import java.util.Objects;
import java.util.UUID;

/**
 * Order ID Value Object
 *
 * 주문 식별자를 나타내는 불변 Value Object
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public class OrderId {

    private final String value;

    /**
     * OrderId 생성자 (외부 직접 호출 금지)
     */
    protected OrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID는 필수입니다.");
        }
        this.value = value;
    }

    /**
     * 새로운 OrderId 생성 (Static Factory)
     *
     * @return 생성된 OrderId
     */
    public static OrderId newOrderId() {
        return new OrderId(UUID.randomUUID().toString());
    }

    /**
     * 기존 값으로 OrderId 생성 (Static Factory)
     *
     * @param value 주문 ID 값
     * @return 생성된 OrderId
     */
    public static OrderId of(String value) {
        return new OrderId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
