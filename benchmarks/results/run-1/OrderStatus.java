package com.company.template.order.domain.model;

/**
 * OrderStatus Enum
 *
 * <p>주문 상태를 표현하는 Enum입니다.</p>
 *
 * <p><strong>상태 전이 규칙:</strong></p>
 * <ul>
 *   <li>PENDING → CONFIRMED (confirm())</li>
 *   <li>PENDING → CANCELLED (cancel())</li>
 *   <li>CONFIRMED → CANCELLED 불가 (비즈니스 규칙)</li>
 * </ul>
 *
 * <p><strong>상태 설명:</strong></p>
 * <ul>
 *   <li>PENDING: 주문 생성 직후 기본 상태</li>
 *   <li>CONFIRMED: 주문이 확정된 상태</li>
 *   <li>CANCELLED: 주문이 취소된 상태</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
public enum OrderStatus {

    /**
     * 주문 생성 상태 (기본값).
     */
    PENDING,

    /**
     * 주문 확정 상태.
     */
    CONFIRMED,

    /**
     * 주문 취소 상태.
     */
    CANCELLED
}
