package com.company.template.order.domain.model;

/**
 * Order 상태
 *
 * <p>주문의 생명주기를 나타냅니다.</p>
 *
 * <ul>
 *   <li>PENDING: 주문 생성 상태</li>
 *   <li>CONFIRMED: 주문 확정 상태</li>
 *   <li>CANCELLED: 주문 취소 상태</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
public enum OrderStatus {
    /**
     * 주문 생성 상태
     */
    PENDING,

    /**
     * 주문 확정 상태
     */
    CONFIRMED,

    /**
     * 주문 취소 상태
     */
    CANCELLED
}
