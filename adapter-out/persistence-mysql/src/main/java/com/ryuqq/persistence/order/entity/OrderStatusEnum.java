package com.ryuqq.persistence.order.entity;

/**
 * Order Status Enum (JPA)
 *
 * JPA Entity에서 사용하는 주문 상태 Enum
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public enum OrderStatusEnum {

    PLACED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
