package com.ryuqq.domain.order;

/**
 * Order Status Enum
 *
 * 주문 상태를 나타내는 Enum
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public enum OrderStatus {

    /**
     * 주문 생성됨 (초기 상태)
     */
    PLACED("주문 완료"),

    /**
     * 주문 확인됨
     */
    CONFIRMED("주문 확인"),

    /**
     * 배송 중
     */
    SHIPPED("배송 중"),

    /**
     * 배송 완료
     */
    DELIVERED("배송 완료"),

    /**
     * 주문 취소됨
     */
    CANCELLED("취소됨");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
